package com.example.eventmanagment;

public class AttendeeModel {

    private String age;
    private String collegeName;
    private String email;
    private String eventTitle;
    private String eventId;       // NEW FIELD
    private String gender;
    private String mobile;
    private String participantName;

    // Empty constructor for Firebase
    public AttendeeModel() {}

    // Constructor with new eventId field
    public AttendeeModel(String age, String collegeName, String email, String eventTitle,
                         String eventId, String gender, String mobile, String participantName) {
        this.age = age;
        this.collegeName = collegeName;
        this.email = email;
        this.eventTitle = eventTitle;
        this.eventId = eventId;
        this.gender = gender;
        this.mobile = mobile;
        this.participantName = participantName;
    }

    // Getters and Setters
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getEventId() { return eventId; }  // NEW
    public void setEventId(String eventId) { this.eventId = eventId; } // NEW

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }
}
