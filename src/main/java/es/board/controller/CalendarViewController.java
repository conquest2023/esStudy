package es.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class CalendarViewController {

    @GetMapping("/calendar")
    public String showCalendarPage() {
        return "basic/feed/Calendar";
    }

    @GetMapping("/todo")
    public String showToDoPage() {
        return "basic/feed/ToDo";

    }

    @GetMapping("/certificate/calendar")
    public String showCertificateCalendar() {
        return "basic/feed/CertificateCalendar";

    }

    @GetMapping("/todo/new")
    public String newTodoForm() {

        return  "basic/feed/ToDoForm";
    }

}