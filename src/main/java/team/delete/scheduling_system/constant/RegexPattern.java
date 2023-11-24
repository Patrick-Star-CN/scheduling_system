package team.delete.scheduling_system.constant;

/**
 * 正则表达式
 *
 * @author patrick_star
 * @version 1.0
 */
public class RegexPattern {
    public static final String USERNAME = "^\\d{5,}$\n";
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,16}$";
    public static final String PHONE_NUMBER = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[235-8]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[0-35-9]\\d{2}|66\\d{2})\\d{6}$";
    public static final String GRADE = "^(2\\d{3})$";
    public static final String STUDENT_NUMBER = "^(20\\d{10})$";
    public static final String DATE = "^(?:(?!0000)[0-9]{4}([-/.]?)(?:(?:0?[1-9]|1[0-2])\\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\\1(?:29|30)|(?:0?[13578]|1[02])\\1(?:31))|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-/.]?)0?2\\2(?:29))$";
}
