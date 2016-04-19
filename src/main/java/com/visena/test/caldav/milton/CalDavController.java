package com.visena.test.caldav.milton;

import io.milton.annotations.AccessControlList;
import io.milton.annotations.Authenticate;
import io.milton.annotations.CalendarColor;
import io.milton.annotations.CalendarDateRangeQuery;
import io.milton.annotations.Calendars;
import io.milton.annotations.ChildOf;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Delete;
import io.milton.annotations.Get;
import io.milton.annotations.ICalData;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Name;
import io.milton.annotations.Principal;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Root;
import io.milton.annotations.UniqueId;
import io.milton.annotations.Users;
import io.milton.resource.AccessControlledResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ResourceController
public class CalDavController {
	Logger log = LoggerFactory.getLogger(getClass());
	/*

	/users/jack/cals/default/abc1234.ics - a single event item

	*/

	private final List<User> users = new ArrayList<User>();

	public CalDavController() {
		createUser("jack", "ja");
		createUser("jill", "ji");
		createUser("bob", "drus"); // http://localhost:9080/visena/dav/users/bob/cals/default/
	}

	@Root
	public CalDavController getRoot() {
		return this;
	}

	@ChildrenOf
	public UsersHome getUsersHome(CalDavController root) {
		return new UsersHome();
	}

	@ChildrenOf
	@Users
	public List<User> getUsers(UsersHome usersHome, @Principal User currentUser) {
		log.debug(String.format("Getting users, auth as: %s", String.valueOf(currentUser)));
		return users;
	}

	// Don't use @Principal here
	@ChildOf
	@Users
	public User findUserByName(UsersHome usersHome, final String userName) {
		User foundUser = users.stream().filter(x -> x.getName().equals(userName)).findFirst().orElse(null);
		log.debug(String.format("findUserByName, found: %s", foundUser != null ? foundUser.getName() : "<null>"));
		return foundUser;
	}
/*
	@ChildOf
	@Users
	public User findUserByName(UsersHome usersHome, final String userName, @Principal User currentUser) {
		log.debug(String.format("findUserByName(as %s): %s", String.valueOf(currentUser), userName));
		User foundUser = users.stream().filter(x -> x.getName().equals(userName)).findFirst().orElse(null);
		log.debug(String.format("findUserByName, found: %s", foundUser != null ? foundUser.getName() : "<null>"));
		return foundUser;
	}
*/

	@ChildrenOf
	public CalendarsHome getCalendarsHome(User user) {
		return new CalendarsHome(user);
	}

	@ChildrenOf
	@Calendars
	public Calendar getCalendarsHome(CalendarsHome cals) {
		return new Calendar(cals.user);
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getRootPrivs(CalDavController target, User currentUser) {
		return AccessControlledResource.READ_BROWSE;
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getUserPrivs(UsersHome target, User currentUser) {
		if (currentUser == null) {
			return AccessControlledResource.NONE;
		} else {
			return AccessControlledResource.READ_BROWSE;
		}
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getCalendarPrivs(Calendar target, User currentUser) {
		log.debug("target.user: " + target.user + ", currentUser: " + currentUser);
		if (currentUser == null) {
			return AccessControlledResource.NONE;
		} else if (target.user.getName().equals(currentUser.getName())) {
			return AccessControlledResource.READ_WRITE;
		} else {
			return AccessControlledResource.READ_BROWSE;
		}
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getMeetingPrivs(Meeting target, User currentUser) {
		log.debug("target.user: " + target.user + ", currentUser: " + currentUser);
		if (currentUser == null) {
			return AccessControlledResource.NONE;
		} else if (target.user.getName().equals(currentUser.getName())) {
			return AccessControlledResource.READ_WRITE;
		} else {
			return AccessControlledResource.READ_BROWSE;
		}
	}

	@CalendarColor
	public String getCalendarColor(Calendar cal) {
		return cal.color;
	}

	@ChildOf
	public Meeting getMeeting(Calendar cal, String uid, @Principal User currentUser) {
//		if (currentUser == null) return null;
		Meeting foundMeeting = cal.user.getMeetings().stream().filter(x -> x.name.equals(uid)).findFirst().orElse(null);
		log.debug("Getting meeting as " + currentUser + ": Found meeting: " + foundMeeting);
		return foundMeeting;
	}

	@ChildrenOf
	public List<Meeting> getCalendar(Calendar cal, @Principal User currentUser) {
		return getCalendarForRange(cal, null, null, currentUser);
	}

	@CalendarDateRangeQuery
	public List<Meeting> getCalendarForRange(Calendar cal, Date fromDate, Date toDate, @Principal User currentUser) {
		if (currentUser == null) {
			log.warn(String.format("currentUser is null (from= %s, to= %s ), returning empty-list", String.valueOf(fromDate), String.valueOf(toDate)));
			return Collections.emptyList();
		}
		log.debug(String.format("As currentUser %s: Getting calendar for user %s, period %s - %s", String.valueOf(currentUser)
			, cal.user.getName(), fromDate, toDate));
		return cal.user.getMeetings();
	}

	@Get
	@ICalData
	public byte[] getMeetingData(Meeting m) {
		return m.getIcalData();
	}

	@PutChild
	public Meeting createMeeting(Calendar cal, byte[] ical, String newName, @Principal User currentUser) {
		if (currentUser == null) {
			return null;
		}
		log.debug("Creating as " + currentUser);
		Meeting m = new Meeting(cal, currentUser);
		m.setIcalData(ical);
		m.name = newName;
		m.id = (System.currentTimeMillis()); // just a unique ID for use with locking and etags
		m.setModifiedDate(new Date());
		cal.user.getMeetings().add(m);
		return m;
	}

	@PutChild
	public Meeting updateMeeting(Meeting m, byte[] ical, @Principal User currentUser) {
		if (currentUser == null) {
			return null;
		}
		log.debug("Updating as " + currentUser);
		m.setIcalData(ical);
		m.setModifiedDate(new Date());
		return m;
	}

	@Delete
	public void deleteMeeting(Meeting m, @Principal User currentUser) {
		if (currentUser != null) {
			log.debug("Deleting as " + currentUser + ": " + m);
			m.cal.user.getMeetings().remove(m);
		}
	}

	@Authenticate
	public Boolean authenticate(User user, String password) {
		log.debug(String.format("Authenticating %s", user.getName()));
		boolean ok = user.getPassword().equals(password);
		log.debug("Auth: " + ok);
		return ok;
	}

	@UniqueId
	public String getUniqueId(Meeting m) {
		return String.valueOf(m.id);
	}

	@Name
	public String getName(Meeting m) {
		return m.name;
	}

	@ModifiedDate
	public Date getModifiedDate(Meeting m) {
		return m.getModifiedDate();
	}

	@CreatedDate
	public Date getCreatedDate(Meeting m) {
		return m.getCreatedDate();
	}

	public final User createUser(String name, String password) {
		User u = new User();
		u.setName(name);
		u.setPassword(password);
		u.setMeetings(new ArrayList<Meeting>());
		users.add(u);
		return u;
	}
}
