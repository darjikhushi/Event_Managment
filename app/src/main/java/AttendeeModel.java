public class AttendeeModel {
    String participantName, collegeName, gender;
    int age;

    public AttendeeModel() {}

    public AttendeeModel(String participantName, String collegeName, int age, String gender) {
        this.participantName = participantName;
        this.collegeName = collegeName;
        this.age = age;
        this.gender = gender;
    }

    public String getParticipantName() { return participantName; }
    public String getCollegeName() { return collegeName; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
}
