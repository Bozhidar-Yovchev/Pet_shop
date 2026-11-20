package com.example.new_final_project.designpatterns.decorator;

import com.example.new_final_project.Repository.NotificationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotifierConfig {

    @Bean
    public Notifier notifier(NotificationRepository notificationRepository) {
        Notifier notifier = new InAppNotifier(notificationRepository);
        notifier = new EmailNotifierDecorator(notifier, notificationRepository);
        notifier = new SmsNotifierDecorator(notifier, notificationRepository);
        return notifier;
    }
}