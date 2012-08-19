package myapp.thukydientu.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.MyFile;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.model.Todo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLUtils {
	// tagName constant

	private static Document parseXml2Document(String xml) {

		Document dom;
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			dom = db.parse(is);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return null;
		} catch (SAXException se) {
			se.printStackTrace();
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (NullPointerException nul) {
			nul.printStackTrace();
			return null;
		}
		return dom;
	}

	public static int getRegisterResult(String xml) {
		Document dom = parseXml2Document(xml);
		if (dom == null) 
			return 0;
		Node result = dom.getElementsByTagName(IConstants.RESULT).item(0);
		if (result.getNodeName().equalsIgnoreCase(IConstants.RESULT)) 
			return Integer.parseInt(result.getFirstChild().getNodeValue());
		return 0;
	}

	public static int[] getLoginResult(String xml) throws NumberFormatException {

		int[] results = new int[] {0, 0};
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return null;
		Node result = dom.getElementsByTagName(IConstants.RESULT).item(0);
		if (result.getNodeName().equalsIgnoreCase(IConstants.RESULT))
			results[0] = Integer.parseInt(result.getFirstChild().getNodeValue());
		Node userId = dom.getElementsByTagName(IConstants.User.ID).item(0);
		if (userId != null)
			if (userId.getNodeName().equalsIgnoreCase(IConstants.User.ID))
				results[1] = Integer.parseInt(userId.getFirstChild().getNodeValue());
		return results;
	}

	public static List<MyFile> getFileResult(String xml) throws NumberFormatException {

		List<MyFile> files = new ArrayList<MyFile>();
		String tmp = "";
		Node fileElement;
		Node childNode;
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return null;
		NodeList elmList = dom.getElementsByTagName("elm");
		if (elmList == null) 
			return null;
		else {
			for (int i = 0; i < elmList.getLength(); i++) {
				Element elm = (Element) elmList.item(i);
	
				MyFile file = new MyFile();
				// fileid
				fileElement = elm.getElementsByTagName(IConstants.File.ID).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				file.setId(Integer.parseInt(tmp));
	
				// filename
				fileElement = elm.getElementsByTagName(IConstants.File.NAME).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				file.setFileName(tmp);
	
				// description
				fileElement = elm.getElementsByTagName(IConstants.File.DESCRIPTION).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				file.setDescription(tmp);
				
				// category
				fileElement = elm.getElementsByTagName(IConstants.File.CATEGORY).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				file.setCategory(tmp);
				
				// format
				fileElement = elm.getElementsByTagName(IConstants.File.FORMAT).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				file.setFormat(tmp);
				
				// size
				fileElement = elm.getElementsByTagName(IConstants.File.SIZE).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				final float roundFloat = Float.parseFloat(tmp);
				file.setSize((long)roundFloat);
				
				// link
				fileElement = elm.getElementsByTagName(IConstants.File.LINK).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				file.setLink(tmp);
				
				// is private
				fileElement = elm.getElementsByTagName(IConstants.File.MODE).item(0);
				childNode = fileElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				file.setIsPrivate(tmp);
				
				files.add(file);
			}
			return files;
		}
	}
	
	public static List<Schedule> getSchedule(String xml) throws NumberFormatException {
		List<Schedule> schedules = new ArrayList<Schedule>();
		Node scheduleElement;
		Node childNode;
		String tmp = "";
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return null;
		NodeList elmList = dom.getElementsByTagName("schedule");
		if (elmList == null) 
			return null;
		else {
			for (int i = 0; i < elmList.getLength(); i++) {
				Element elm = (Element) elmList.item(i);
	
				Schedule schedule = new Schedule();
				
				// dateName
				scheduleElement = elm.getElementsByTagName(ScheduleTable.DAY_NAME).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = "1";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "dateName: " + tmp);
				schedule.setDayName(Integer.parseInt(tmp));
	
				// time
				scheduleElement = elm.getElementsByTagName(ScheduleTable.TIME).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "time: " + tmp);
				schedule.setTime(tmp);
				
				// subject
				scheduleElement = elm.getElementsByTagName(ScheduleTable.SUBJECT).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "subject: " + tmp);
				schedule.setSubject(tmp);
				
				// class
				scheduleElement = elm.getElementsByTagName(ScheduleTable.CLASS).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "class: " + tmp);
				schedule.setClassName(tmp);
				
				// school
				scheduleElement = elm.getElementsByTagName(ScheduleTable.SCHOOL).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "school: " + tmp);
				schedule.setSchoolName(tmp);
				
				// dateSet
				scheduleElement = elm.getElementsByTagName(ScheduleTable.CREATED).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "dateSet: " + tmp);
				schedule.setDateSet(tmp);
				
				// modified
				scheduleElement = elm.getElementsByTagName(ScheduleTable.MODIFIED).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "modified: " + tmp);
				schedule.setModified(tmp);
				
				// deleted
				scheduleElement = elm.getElementsByTagName(ScheduleTable.DELETED).item(0);
				childNode = scheduleElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Schedule from xml", "deleted: " + tmp);
				schedule.setDeleted(Integer.parseInt(tmp));
				
				schedules.add(schedule);
			}
			return schedules;
		}
	}

	public static List<Todo> getTodo(String xml) throws NumberFormatException {
		List<Todo> todos = new ArrayList<Todo>();
		Node todoElement;
		Node childNode;
		String tmp = "";
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return null;
		NodeList elmList = dom.getElementsByTagName("todo");
		if (elmList == null) 
			return null;
		else {
			for (int i = 0; i < elmList.getLength(); i++) {
				Todo todo = new Todo();
				Element elm = (Element) elmList.item(i);
	
				// dateStart
				todoElement = elm.getElementsByTagName(TodoTable.DATE_START).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "dateStart: " + tmp);
				todo.setDateStart(tmp);
	
				// dateEnd
				todoElement = elm.getElementsByTagName(TodoTable.DATE_END).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "dateEnd: " + tmp);
				todo.setDateEnd(tmp);
				
				// timeFrom
				todoElement = elm.getElementsByTagName(TodoTable.TIME_FROM).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "timeFrom: " + tmp);
				todo.setTimeFrom(tmp);

				// timeUntil
				todoElement = elm.getElementsByTagName(TodoTable.TIME_UNTIL).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "timeUntil: " + tmp);
				todo.setTimeUntil(tmp);

				// title
				todoElement = elm.getElementsByTagName(TodoTable.TITLE).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "title: " + tmp);
				todo.setTitle(tmp);
				
				// work
				todoElement = elm.getElementsByTagName(TodoTable.WORK).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = "";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "work: " + tmp);
				todo.setWork(tmp);
				
				// alarm
				todoElement = elm.getElementsByTagName(TodoTable.ALARM).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "alarm: " + tmp);
				todo.setAlarm(tmp == "0" ? 0 : 1);
				
				// dateSet
				todoElement = elm.getElementsByTagName(TodoTable.DATE_SET).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "dateSet: " + tmp);
				todo.setDateSet(tmp);
				
				// modified
				todoElement = elm.getElementsByTagName(TodoTable.MODIFIED).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "modified: " + tmp);
				todo.setModified(tmp);
				Log.d("Todo from xml", "todo.modified: " + todo.getModified());
				
				// deleted
				todoElement = elm.getElementsByTagName(TodoTable.DELETED).item(0);
				childNode = todoElement.getFirstChild();
				if (childNode == null) 
					tmp = "0";
				else 
					tmp = childNode.getNodeValue();
				Log.d("Todo from xml", "deleted: " + tmp);
				todo.setDeleted(Integer.parseInt(tmp));
				
				todos.add(todo);
			}
			return todos;
		}
	}
	
	public static int[] addScheduleResult(String xml) {
		int[] results = new int[] {0, 0};
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return null;
		Node result = dom.getElementsByTagName(IConstants.RESULT).item(0);
		if (result.getNodeName().equalsIgnoreCase(IConstants.RESULT)) 
			results[0] = Integer.parseInt(result.getFirstChild().getNodeValue());
		Node scheduleId = dom.getElementsByTagName(IConstants.User.ID).item(0);
		if (scheduleId != null)
			if (scheduleId.getNodeName().equalsIgnoreCase(IConstants.WebServices.SCHEDULE_ID))
				results[1] = Integer.parseInt(scheduleId.getFirstChild().getNodeValue());
		return results;
	}
	
	public static int addNoticeResult(String xml) {
		Document dom = parseXml2Document(xml);
		if (dom == null)
			return 0;
		Node result = dom.getElementsByTagName(IConstants.RESULT).item(0);
		if (result.getNodeName().equalsIgnoreCase(IConstants.RESULT)) 
			return Integer.parseInt(result.getFirstChild().getNodeValue());
		return 0;
	}
}
