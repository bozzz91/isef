package ru.desu.home.isef.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collections;

@Component("geo")
public class GeoUtil {

	private GeoUtil() {}

	public String[] detectCountry(String ip) throws IOException, GeoIp2Exception {
		InputStream dbStream = GeoUtil.class.getResourceAsStream("/GeoLite2-Country.mmdb");
		DatabaseReader reader = new DatabaseReader.Builder(dbStream).locales(Collections.singletonList("ru")).build();
		InetAddress ipAddress = InetAddress.getByName(ip);
		CountryResponse response = reader.country(ipAddress);
		Country country = response.getCountry();
		return new String[] {country.getIsoCode(), country.getName()};
	}
}
