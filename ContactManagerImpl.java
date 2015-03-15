import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;


public class ContactManagerImpl implements ContactManager {
	private List<Contact> contacts;
//	private List<Meeting> futureMeetings;
//	private List<Meeting> pastMeetings;
	private List<Meeting> meetings;
	private Timer timer;
	private Scheduler scheduler;
	private final int DELAY = 0;
	private final int PERIOD = 1000;
	
	public ContactManagerImpl() {
		contacts = new ArrayList();
//		futureMeetings = new ArrayList();
//		pastMeetings = new ArrayList();
		meetings = new ArrayList();
		scheduler = new Scheduler(this);
		timer = new Timer();
		timer.schedule(scheduler, DELAY, PERIOD);
		
		//Creating new Contacts
		Contact Pete = new ContactImpl("Pete Jones", "Marketing manager");
		Contact Tom = new ContactImpl("Tom Hanks", "Actor"); 
		Contact Mary = new ContactImpl("Mary");
		
		contacts.add(Pete);
		contacts.add(Tom);
		contacts.add(Mary);
		
		//Creating new Meetings
		Calendar pijDate;	//id = 1
		Meeting pij;
		Set<Contact> pijContacts = new HashSet<Contact>();
		pijContacts.add(Pete);
		pijContacts.add(Mary);
		pijDate = Calendar.getInstance();
		pijDate.set(2015, 5, 9, 10, 00);
		pij = new FutureMeetingImpl(pijContacts, pijDate);

		Calendar sdpDate;	//id = 2
		Meeting sdp;
		Set<Contact> sdpContacts = new HashSet<Contact>();
		sdpContacts.add(Tom);
		sdpContacts.add(Mary);
		sdpDate = Calendar.getInstance();
		sdpDate.set(2015, 4, 26, 14, 30);
		sdp = new FutureMeetingImpl(sdpContacts, sdpDate);
		
		Calendar focDate;	//id = 3
		Meeting foc;
		Set<Contact> focContacts = new HashSet<Contact>();
		focContacts.add(Tom);
		focContacts.add(new ContactImpl("Mark", "examiner")	);
		focDate = Calendar.getInstance();
		focDate.set(2014, 4, 22, 11, 30);
		foc = new PastMeetingImpl(focContacts, focDate, "FoC exam");
		
		meetings.add(pij);
		meetings.add(sdp);
		meetings.add(foc);

		//Misc
		SimpleDateFormat df;
		df = new SimpleDateFormat("yyyy.MMMMM.dd HH:mm");
	}
	
	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		Meeting addMeeting = new FutureMeetingImpl(contacts, date);
		meetings.add(addMeeting);
		return addMeeting.getId();
	}

	@Override
	public PastMeeting getPastMeeting(int id) {
		List<Meeting> resultList = new ArrayList();
		getPastMeetings().stream()
						 .filter(m -> m.getId() == id)
						 .forEach(m -> resultList.add(m));

		if(resultList.size() > 1) {
			throw new IllegalStateException();
		}
		return (PastMeeting) resultList.get(0);
	}

	@Override
	public FutureMeeting getFutureMeeting(int id) {
		List<Meeting> resultList = new ArrayList();
		getFutureMeetings().stream()
						   .filter(m -> m.getId() == id)
						   .forEach(m -> resultList.add(m));

		if(resultList.size() > 1) {
			throw new IllegalStateException();
		}
		return (FutureMeeting) resultList.get(0);
	}

	@Override
	public Meeting getMeeting(int id) {
		List<Meeting> resultList = new ArrayList();
		meetings.stream()
				.filter(m -> m.getId() == id)
				.forEach(m -> resultList.add(m));
		
		if(resultList.size() > 1) {
			throw new IllegalStateException();
		}
		return resultList.get(0);
	}

	@Override
	public List<Meeting> getFutureMeetingList(Contact contact) {
		List<Meeting> result = new ArrayList();
		for(Meeting m: getFutureMeetings()) {
			for(Contact c: m.getContacts()) {
				if(c.getId() == contact.getId()) {
					result.add(m);
				}
			}
		}
		return result;
	}

	@Override
	public List<Meeting> getFutureMeetingList(Calendar date) {
		List<Meeting> result = new ArrayList();
		for(Meeting m: getFutureMeetings()) {
			m.getDate();
			m.getDate();
			if(m.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR) && m.getDate().get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)){
				result.add(m);
			}
		}
		return result;
	}

	@Override
	public List<PastMeeting> getPastMeetingList(Contact contact) {
		List<PastMeeting> result = new ArrayList();
		for(Meeting m: getPastMeetings()) {
			for(Contact c: m.getContacts()) {
				if(c.getId() == contact.getId()) {
					result.add((PastMeeting) m);
				}
			}
		}
		return result;
	}

	@Override
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
		Meeting addMeeting = new PastMeetingImpl(contacts, date, text);
		meetings.add(addMeeting);
	}

	@Override
	public void addMeetingNotes(int id, String text) {
		PastMeetingImpl pm = (PastMeetingImpl) getPastMeeting(id);
		pm.setNotes(text);
	}

	@Override
	public void addNewContact(String name, String notes) {
		contacts.add(new ContactImpl(name, notes));		
	}

	@Override
	public Set<Contact> getContacts(int... ids) {
		Set<Contact> result = new HashSet();
		for (int i: ids) {
			contacts.stream()
					.filter(c -> c.getId() == i)
					.forEach(c -> result.add(c));
		}
		return result;
	}

	@Override
	public Set<Contact> getContacts(String name) {
		Set<Contact> result = new HashSet();
		contacts.stream()
				.filter(c -> c.getName().equals(name))
				.forEach(c -> result.add(c));		
		return result;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns all contacts.
	 * @return List of contacts. 
	 */
	public List<Contact> getCMContacts() {
		return contacts;
	}
	
	/**
	 * Returns all meetings.
	 * @return List of meetings.
	 */
	public List<Meeting> getMeetings() {
		return meetings;
	}
	
	/**
	 * Returns a list of past meetings.
	 * 
	 * @return List of past meetings. 
	 */
	public List<Meeting> getPastMeetings() {
		List<Meeting> result = new ArrayList();
		meetings.stream()
				.filter(c -> c.getClass().getName().equals("PastMeetingImpl"))
				.forEach(c -> result.add(c));
		return result;
	}
	
	/**
	 * Returns a list of future meetings.
	 * 
	 * @return List of future meetings. 
	 */
	public List<Meeting> getFutureMeetings() {
		List<Meeting> result = new ArrayList();
		meetings.stream()
				.filter(c -> c.getClass().getName().equals("FutureMeetingImpl"))
				.forEach(c -> result.add(c));
		return result;
	}
	
	/**
	 * Stops the timer running. 
	 */
	public void shutOffTimer() {
		timer.cancel();
	}
	
	/**
	 * The timer calls this method according to a preset interval in order to convert future meeting 
	 * to past meetings in case the meeting has already taken place. 
	 */
	public void revaluateMeetings() {
		Calendar now = Calendar.getInstance();
		List<Meeting> convert = new ArrayList();
		getFutureMeetings().stream()
						   .filter(m -> m.getDate().getTime().getTime() < now.getTime().getTime())
						   .forEach(m -> convert.add(m));
		
		for(Meeting m: convert) {
			convertMeeting(m);
		}
	}
	
	/**
	 * Converts a future meeting into a past meeting by locating the future meeting in the meetings list and 
	 * replacing it with a new past meeting with the same parameters as the future meeting. 
	 * 
	 * @param m the future meeting to be converted into a past meeting. 
	 */
	public void convertMeeting(Meeting m) {
		int i = 0;
		Meeting pastTemp;
		Meeting futureTemp;
		while(meetings.get(i).getId() != m.getId() && i < meetings.size()) {
			futureTemp = meetings.get(i);
			pastTemp = new PastMeetingImpl(futureTemp.getContacts(), futureTemp.getDate(), futureTemp.getId());
			meetings.set(i, pastTemp);
			i++;
		}
	}
	
	/**
	 * The method is to be used to convert future meetings into past meetings. 
	 * 
	 * @param m the future meeting to be converted into a past meeting.
	 * @param notes to be added to the meeting. 
	 */
	public void manualMeetingConversion(Meeting m, String notes) {
		convertMeeting(m);
		addMeetingNotes(m.getId(), notes);
	}
}
