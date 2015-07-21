package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Captcha;

public interface CaptchaRepo extends JpaRepository<Captcha, Long> {

}
