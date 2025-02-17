package es.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarPageController {

    @GetMapping("/calendar")  // 🔹 /calendar로 접속하면 HTML 반환
    public String showCalendarPage() {
        return "basic/feed/Calendar";  // 🔹 resources/templates/calendar.html을 반환
    }

    @GetMapping("/todo")  // 🔹 /calendar로 접속하면 HTML 반환
    public String showToDoPage() {
        return "basic/feed/ToDo";

    }

    @GetMapping("/todo/new")
    public String newTodoForm() {

        return  "basic/feed/ToDoForm";
    }

}