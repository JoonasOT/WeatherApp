package fi.tuni.prog3.weatherapp.frontend;

public enum WeatherFont {
    THUNDER_LIGHT_RAIN(200, 76),    THUNDER_RAIN(201, 78),
    THUNDER_HEAVY_RAIN(202, 80),    THUNDER_LIGHT(210, 82),
    THUNDER(211, 84),               THUNDER_HEAVY(212, 86),
    THUNDER_RAGGED(221, 88),        THUNDER_LIGHT_DRIZZLE(230, 90),
    THUNDER_DRIZZLE(231, 92),       THUNDER_HEAVY_DRIZZLE(232, 94),

    DRIZZLE_LIGHT(300, 97),         DRIZZLE(301, 99),
    DRIZZLE_HEAVY(302, 101),         DRIZZLE_LIGHT_RAIN(310, 103),
    DRIZZLE_RAIN(311, 105),          DRIZZLE_HEAVY_RAIN(312, 107),
    DRIZZLE_SHOWER_RAIN(313, 109),   DRIZZLE_HEAVY_SHOWER_RAIN(314, 111),
    DRIZZLE_SHOWER(321, 113),

    RAIN_LIGHT(500, 103),              RAIN_MODERATE(501, 105),
    RAIN_HEAVY(502, 107),              RAIN_VERY_HEAVY(503, 115),
    RAIN_EXTREME(504, 115),            RAIN_FREEZING(511, 105),
    RAIN_SHOWER_LIGHT(520, 109),       RAIN_SHOWER(521, 113),
    RAIN_SHOWER_HEAVY(522, 111),       RAIN_SHOWER_RAGGED(531, 113),

    SNOW_LIGHT(600, 118),            SNOW(601, 120),
    SNOW_HEAVY(602, 122),            SLEET(611, 124),
    SLEET_LIGHT(612, 126),           SLEET_SHOWER(613, 124),
    SNOW_RAIN_LIGHT(615, 126),       SNOW_RAIN(616, 124),
    SNOW_SHOWER_LIGHT(620, 126),     SNOW_SHOWER(621, 124),
    SNOW_SHOWER_HEAVY(622, 124),

    MIST(701, 129),                  SMOKE(711, 129),
    HAZE(721, 129),                  DUST_SAND(731, 129),
    FOG(741, 129),                   SAND(751, 129),
    DUST(761, 129),                  ASH(762, 129),
    SQUALL(771, 129),                TORNADO(781, 132),

    CLEAR(800, 35),

    CLOUDS_FEW(801, 37),            CLOUDS_SCATTERED(802, 39),
    CLOUDS_BROKEN(803, 41),         CLOUDS_OVERCAST(804, 43);
    
    public static final String LOCATION = "file:./res/font/weather.ttf";
    public final int code;
    public final int offset;
    WeatherFont(int code, int offset) {
        this.code = code;
        this.offset = offset;
    }
    public static String CodeToChar(int code, boolean isDay) {
        for (var v : WeatherFont.values()) {
            if (v.code == code) return String.valueOf((char)(v.offset + (isDay ? 0 : 1)));
        }
        System.err.println("Invalid weather code: " + Integer.toString(code));
        return "";
    }
}
