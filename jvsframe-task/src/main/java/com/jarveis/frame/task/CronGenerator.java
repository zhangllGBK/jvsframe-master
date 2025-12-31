package com.jarveis.frame.task;

import com.jarveis.frame.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Cron表达式解析器和生成器
 * <pre>
 *     CRON表达式是一个字符串，包含六个由空格分隔的字段，表示一组时间，通常作为执行某个程序的时间表。
 *
 *     例子：
 *     # 每月的第1天的1点钟执行
 *     0 0 1 1 * *
 *
 *     说明：
 *     *    *    *    *    *    *
 *     -    -    -    -    -    -
 *     |    |    |    |    |    |
 *     |    |    |    |    |    +----- day of week (0 - 7) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
 *     |    |    |    |    +----- month (1 - 12) OR jan,feb,mar,apr ...
 *     |    |    |    +---------- day of month (1 - 31)
 *     |    |    +--------------- hour (0 - 23)
 *     |    +-------------------- minute (0 - 59)
 *     +------------------------- seconds (0 - 59)
 *
 *     字段           是否必填	允许值	        允许特殊字符
 *     Seconds	     是	        0–59	        *,-/
 *     Minutes	     是	        0–59	        *,-/
 *     Hours	     是	        0–23	        *,-/
 *     Day of month  是	        1–31	        *,-/
 *     Month	     是	        1–12 or JAN–DEC	*,-/
 *     Day of week   是	        0–7 or SUN–SAT	*,-
 *
 *     ","(逗号)用于分隔列表。例如，在第5个字段(星期几)中使用 MON,WED,FRI 表示周一、周三和周五。
 *     "-"(连字符)定义范围。例如，2000-2010 表示2000年至2010年期间的每年，包括2000年和2010年。
 *     "/"(除号符)定义步长。例如，星号/10 表示从0开始，每10秒运行一次。
 * </pre>
 *
 * @author liuguojun
 * @since 2024-10-23
 */
public class CronGenerator {

    private final BitSet seconds = new BitSet(60);

    private final BitSet minutes = new BitSet(60);

    private final BitSet hours = new BitSet(24);

    private final BitSet daysOfWeek = new BitSet(7);

    private final BitSet daysOfMonth = new BitSet(31);

    private final BitSet months = new BitSet(12);

    private final String expression;

    private final TimeZone timeZone;

    public CronGenerator(String expression) {
        this(expression, TimeZone.getDefault());
    }

    public CronGenerator(String expression, TimeZone timeZone) {
        this.expression = expression;
        this.timeZone = timeZone;
        parse(expression);
    }

    /**
     * 解析表达式
     *
     * @param expression  表达式
     * @throws IllegalArgumentException  表达式错误
     */
    private void parse(String expression) throws IllegalArgumentException {
        String[] fields = StringUtils.split(expression, " ");
        if (fields.length != 6) {
            throw new IllegalArgumentException(String.format("cron表达式需要6个字段(发现给定的表达式:%s, 只有%d个)", expression, fields.length));
        }
        setNumberHits(this.seconds, fields[0], 0, 60);
        setNumberHits(this.minutes, fields[1], 0, 60);
        setNumberHits(this.hours, fields[2], 0, 24);
        setDaysOfMonth(this.daysOfMonth, fields[3]);
        setMonths(this.months, fields[4]);
        setDays(this.daysOfWeek, replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
        if (this.daysOfWeek.get(7)) {
            // Sunday can be represented as 0 or 7
            this.daysOfWeek.set(0);
            this.daysOfWeek.clear(7);
        }
    }

    /**
     * 返回给定时间的下次触发时间
     *
     * @param date 给定时间
     * @return 下次触发时间
     */
    public Date next(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);

        calendar.add(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);

        doNext(calendar, calendar.get(Calendar.YEAR));

        return calendar.getTime();
    }

    /**
     * 寻找下一个匹配的日期
     *
     * @param calendar  日历
     * @param dot  日期
     */
    private void doNext(Calendar calendar, int dot) {
        List<Integer> resets = new ArrayList<Integer>();

        int second = calendar.get(Calendar.SECOND);
        List<Integer> emptyList = Collections.emptyList();
        int updateSecond = findNext(this.seconds, second, calendar, Calendar.SECOND, Calendar.MINUTE, emptyList);
        if (second == updateSecond) {
            resets.add(Calendar.SECOND);
        }

        int minute = calendar.get(Calendar.MINUTE);
        int updateMinute = findNext(this.minutes, minute, calendar, Calendar.MINUTE, Calendar.HOUR_OF_DAY, resets);
        if (minute == updateMinute) {
            resets.add(Calendar.MINUTE);
        } else {
            doNext(calendar, dot);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int updateHour = findNext(this.hours, hour, calendar, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_WEEK, resets);
        if (hour == updateHour) {
            resets.add(Calendar.HOUR_OF_DAY);
        } else {
            doNext(calendar, dot);
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int updateDayOfMonth = findNextDay(calendar, this.daysOfMonth, dayOfMonth, daysOfWeek, dayOfWeek, resets);
        if (dayOfMonth == updateDayOfMonth) {
            resets.add(Calendar.DAY_OF_MONTH);
        } else {
            doNext(calendar, dot);
        }

        int month = calendar.get(Calendar.MONTH);
        int updateMonth = findNext(this.months, month, calendar, Calendar.MONTH, Calendar.YEAR, resets);
        if (month != updateMonth) {
            if (calendar.get(Calendar.YEAR) - dot > 4) {
                throw new IllegalStateException("Invalid cron expression led to runaway search for next trigger");
            }
            doNext(calendar, dot);
        }

    }

    /**
     * 寻找下一个匹配的日期
     *
     * @param calendar  日历
     * @param daysOfMonth  日期
     * @param dayOfMonth  日期
     * @param daysOfWeek  星期几
     * @param dayOfWeek  星期几
     * @param resets  重置的日期
     * @return  下一个匹配的日期
     */
    private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
        int count = 0;
        int max = 366;
        // the DAY_OF_WEEK values in java.util.Calendar start with 1 (Sunday),
        // but in the cron pattern, they start with 0, so we subtract 1 here
        while ((!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) && count++ < max) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            reset(calendar, resets);
        }
        if (count >= max) {
            throw new IllegalStateException("Overflow in day for expression=" + this.expression);
        }
        return dayOfMonth;
    }

    /**
     * 寻找下一个匹配的日期
     *
     * @param bits  日期
     * @param value  日期
     * @param calendar  日历
     * @param field  日期字段
     * @param nextField  下一个日期字段
     * @param lowerOrders  重置的日期字段
     * @return  下一个匹配的日期
     */
    private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
        int nextValue = bits.nextSetBit(value);
        // roll over if needed
        if (nextValue == -1) {
            calendar.add(nextField, 1);
            reset(calendar, Arrays.asList(field));
            nextValue = bits.nextSetBit(0);
        }
        if (nextValue != value) {
            calendar.set(field, nextValue);
            reset(calendar, lowerOrders);
        }
        return nextValue;
    }

    /**
     * 重置日期字段
     *
     * @param calendar  日历
     * @param fields  重置的日期字段
     */
    private void reset(Calendar calendar, List<Integer> fields) {
        for (int field : fields) {
            calendar.set(field, field == Calendar.DAY_OF_MONTH ? 1 : 0);
        }
    }

    /**
     * 替换星期几
     *
     * @param value  星期几
     * @param commaSeparatedList  星期几列表
     * @return  替换后的星期几
     */
    private String replaceOrdinals(String value, String commaSeparatedList) {
        String[] list = StringUtils.split(commaSeparatedList, ",");
        for (int i = 0; i < list.length; i++) {
            String item = list[i].toUpperCase();
            value = StringUtils.replace(value.toUpperCase(), item, "" + i);
        }
        return value;
    }

    /**
     * 设置日期
     *
     * @param bits  日期
     * @param field  日期字段
     */
    private void setDaysOfMonth(BitSet bits, String field) {
        int max = 31;
        // Days of month start with 1 (in Cron and Calendar) so add one
        setDays(bits, field, max + 1);
        // ... and remove it from the front
        bits.clear(0);
    }

    /**
     * 设置日期
     *
     * @param bits  日期
     * @param field  日期字段
     * @param max  日期最大值
     */
    private void setDays(BitSet bits, String field, int max) {
        if (field.contains("?")) {
            field = "*";
        }
        setNumberHits(bits, field, 0, max);
    }

    /**
     * 设置月份
     *
     * @param bits  月份
     * @param value  月份值
     */
    private void setMonths(BitSet bits, String value) {
        int max = 12;
        value = replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
        BitSet months = new BitSet(13);
        // Months start with 1 in Cron and 0 in Calendar, so push the values first into a longer bit set
        setNumberHits(months, value, 1, max + 1);
        // ... and then rotate it to the front of the months
        for (int i = 1; i <= max; i++) {
            if (months.get(i)) {
                bits.set(i - 1);
            }
        }
    }

    /**
     * 设置星期几
     *
     * @param bits  星期几
     * @param value  星期几值
     */
    private void setNumberHits(BitSet bits, String value, int min, int max) {
        String[] fields = StringUtils.split(value, ",");
        for (String field : fields) {
            if (!field.contains("/")) {
                // Not an incrementer so it must be a range (possibly empty)
                int[] range = getRange(field, min, max);
                bits.set(range[0], range[1] + 1);
            } else {
                String[] split = StringUtils.split(field, "/");
                if (split.length > 2) {
                    throw new IllegalArgumentException("Incrementer has more than two fields: " + field);
                }
                int[] range = getRange(split[0], min, max);
                if (!split[0].contains("-")) {
                    range[1] = max - 1;
                }
                int delta = Integer.valueOf(split[1]);
                for (int i = range[0]; i <= range[1]; i += delta) {
                    bits.set(i);
                }
            }
        }
    }

    /**
     * 获取范围
     *
     * @param field  范围值
     * @param min  最小值
     * @param max  最大值
     * @return  范围
     */
    private int[] getRange(String field, int min, int max) {
        int[] result = new int[2];
        if (field.contains("*")) {
            result[0] = min;
            result[1] = max - 1;
            return result;
        }
        if (!field.contains("-")) {
            result[0] = result[1] = Integer.valueOf(field);
        } else {
            String[] split = StringUtils.split(field, "-");
            if (split.length > 2) {
                throw new IllegalArgumentException("Range has more than two fields: " + field);
            }
            result[0] = Integer.valueOf(split[0]);
            result[1] = Integer.valueOf(split[1]);
        }
        if (result[0] >= max || result[1] >= max) {
            throw new IllegalArgumentException("Range exceeds maximum (" + max + "): " + field);
        }
        if (result[0] < min || result[1] < min) {
            throw new IllegalArgumentException("Range less than minimum (" + min + "): " + field);
        }
        return result;
    }

    public static void main(String[] args) {
        CronGenerator cronGenerator = new CronGenerator("0 0 1 1 1 *");
        Date now = DateUtil.getDate();
        for (int i = 0; i < 5; i++) {
            Date nextDate = cronGenerator.next(now);
            System.out.println(DateUtil.getDateStr(nextDate, DateUtil.FORMAT_YMDHMS));
            now = nextDate;
        }
    }

}
