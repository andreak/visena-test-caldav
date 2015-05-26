package com.visena.test.caldav.milton;

import com.hellocaldav.Calendar;
import com.hellocaldav.CalendarsHome;
import com.hellocaldav.Meeting;
import com.hellocaldav.User;
import com.hellocaldav.UsersHome;
import io.milton.annotations.CalendarDateRangeQuery;
import io.milton.annotations.Calendars;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.Get;
import io.milton.annotations.ICalData;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Name;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.Root;
import io.milton.annotations.UniqueId;
import io.milton.annotations.Users;
import io.milton.resource.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ResourceController
public class CalDavController {
	/*

	/users/jack/cals/default/abc1234.ics - a single event item

	*/

	private final List<User> users = new ArrayList<User>();

	public CalDavController() {
		createUser("jack");
		createUser("jill");
		createUser("bob");
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		System.out.println(String.format("Getting users, auth as: %s", authentication.getPrincipal()));
		return users;
	}

	@Name
	public String getBolle(Resource resource) {
		System.out.println(String.format("Resource %s, uniqueID: %s", resource.getName(), resource.getUniqueId()));
		return "visena";
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
		return cal.user.getMeetings();
	}

/*
	@CalendarDateRangeQuery
	public List<Meeting> getCalendarForRange(Calendar cal, Date fromDate, Date toDate) {
		System.out.println(String.format("Getting calendar %s - %s", fromDate, toDate));
		return cal.user.getMeetings();
	}
*/

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

	@UniqueId
	public long getUniqueId(Meeting m) {
		return m.getId();
	}

	@ModifiedDate
	public Date getModifiedDate(Meeting m) {
		return m.getModifiedDate();
	}

	public final User createUser(String name) {
		User u = new User();
		u.setName(name);
		u.setMeetings(new ArrayList<Meeting>());
		users.add(u);
		return u;
	}

}
