package at.phiber93.myfirstwebapp.todo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("name")
public class TodoController {
    private TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @RequestMapping("list-todos")
    public String listAllTodos(ModelMap modelMap) {
        String username = getLoggedinUsername();

        List<Todo> todos = todoRepository.findByUsername(username);
        modelMap.addAttribute("todos", todos);
        return "listTodos";
    }

    @RequestMapping(value = "add-todo", method = RequestMethod.GET)
    public String showNewTodoPage(ModelMap modelMap) {
        String username = getLoggedinUsername();
        Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
        modelMap.put("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "add-todo", method = RequestMethod.POST)
    public String addNewTodo(@Valid Todo todo, BindingResult result) {
        if (result.hasErrors()) {
            return "todo";
        }

        String username = getLoggedinUsername();
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id) {

        todoRepository.deleteById(id);
        return "redirect:list-todos";
    }

    @RequestMapping(value = "update-todo", method = RequestMethod.GET)
    public String showUpdateTodoPage(@RequestParam int id, ModelMap modelMap) {
        Todo todo = todoRepository.findById(id).get();
        modelMap.addAttribute("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "update-todo", method = RequestMethod.POST)
    public String updateTodo(@Valid Todo todo, BindingResult result) {
        if (result.hasErrors()) {
            return "todo";
        }

        String username = getLoggedinUsername();
        todo.setUsername(username);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }

    
    private String getLoggedinUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();
    }
}
