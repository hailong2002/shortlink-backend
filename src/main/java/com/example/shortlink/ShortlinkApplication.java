package com.example.shortlink;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;
import java.util.TimeZone;

@SpringBootApplication
public class ShortlinkApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		// FIX LỖI TREO 502/504 GATEWAY CỦA CLOUDFLARE/NGINX:
		// Ép Java chỉ sử dụng IPv4 khi gọi API của Google (oauth2.googleapis.com).
		// Rất nhiều VPS Ubuntu/Docker bị lỗi định tuyến IPv6, khiến lệnh gọi API backend bị treo vĩnh viễn
		// (không báo lỗi, không crash, chỉ im lìm đến khi Cloudflare văng 502/504 timeout).
//		System.setProperty("java.net.preferIPv4Stack", "true");
		SpringApplication.run(ShortlinkApplication.class, args);
	}

}
