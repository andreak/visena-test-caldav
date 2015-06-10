package com.visena.test.caldav.milton;

import io.milton.annotations.Authenticate;
import io.milton.annotations.CalendarDateRangeQuery;
import io.milton.annotations.Calendars;
import io.milton.annotations.ChildOf;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Delete;
import io.milton.annotations.Get;
import io.milton.annotations.ICalData;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Root;
import io.milton.annotations.UniqueId;
import io.milton.annotations.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
	public List<User> getUsers(UsersHome usersHome) {
//		log.debug(String.format("Getting users, auth as: %s", authentication.getPrincipal()));
		return users;
	}

	@ChildOf
	@Users
	public User findUserByName(UsersHome usersHome, final String userName) {
		log.debug(String.format("findUserByName: %s", userName));
		User foundUser = users.stream().filter(x -> x.getName().equals(userName)).findFirst().orElse(null);
		log.debug(String.format("findUserByName, found: %s", foundUser != null ? foundUser.getName() : "<null>"));
		return foundUser;
	}

	@ChildrenOf
	public CalendarsHome getCalendarsHome(User user) {
		return new CalendarsHome(user);
	}

	@ChildrenOf
	@Calendars
	public Calendar getCalendarsHome(CalendarsHome cals) {
		return new Calendar(cals.user);
	}

	@ChildrenOf
	public List<Meeting> getCalendar(Calendar cal) {
		return getCalendarForRange(cal, null, null);
	}

	@CalendarDateRangeQuery
	public List<Meeting> getCalendarForRange(Calendar cal, Date fromDate, Date toDate) {
		log.debug(String.format("Getting calendar for user %s, period %s - %s", cal.user.getName(), fromDate, toDate));
		return cal.user.getMeetings();
	}

	@Get
	@ICalData
	public byte[] getMeetingData(Meeting m) {
		return m.getIcalData();
	}

	@PutChild
	public Meeting createMeeting(Calendar cal, byte[] ical, String newName) {
		Meeting m = new Meeting();
		m.setIcalData(ical);
		m.setName(newName);
		m.setId(System.currentTimeMillis()); // just a unique ID for use with locking and etags
		m.setModifiedDate(new Date());
		cal.user.getMeetings().add(m);
		return m;
	}

	@PutChild
	public Meeting updateMeeting(Meeting m, byte[] ical) {
		m.setIcalData(ical);
		m.setModifiedDate(new Date());
		return m;
	}

	@Authenticate
	public Boolean authenticate(User user, String password) {
		log.debug(String.format("Authenticating %s", user.getName()));
		boolean ok = user.getPassword().equals(password);
		log.debug("Auth: " + ok);
		return ok;
	}

	@UniqueId
	public long getUniqueId(Meeting m) {
		return m.getId();
	}

	@ModifiedDate
	public Date getModifiedDate(Meeting m) {
		return m.getModifiedDate();
	}

	@CreatedDate
	public Date getCreatedDate(Meeting m) {
		return m.getCreatedDate();
	}

	@Delete
	public void deleteMeeting(Calendar cal, Meeting m) {
		log.debug("Deleting" + m);
		cal.user.getMeetings().remove(m);
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
