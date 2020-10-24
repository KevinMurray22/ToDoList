package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import com.example.servingwebcontent.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Controller
public class DashboardController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;
   @GetMapping("/")
    public String dashboardIndexForm(@CurrentSecurityContext(expression="authentication.name")
    String username, RedirectAttributes redirectAttributes, Model model, @RequestParam(defaultValue = "") String categoryName) {

        updateExpOfItems(username);
        if(categoryName.equals("")) {

            model.addAttribute("listOfCategories", categoryRepository.findByParentCategoryAndUserName("Root", username));
       }
        else {
            if(categoryRepository.findByCategoryAndUserName(categoryName, username).isHasChild()) {

                model.addAttribute("listOfCategories", categoryRepository.findByParentCategoryAndUserName(categoryName, username));
            }
            else {

                redirectAttributes.addAttribute("categoryName", categoryName);
                return "redirect:/list";
            }
        }
        System.out.println("Username :" + username);
        model.addAttribute("currentUser", username);
        return "index.html";
    }

    @GetMapping("/createItem")
    public String createItemForm(@CurrentSecurityContext(expression="authentication.name") String username, Model model) {
        Item item = new Item();
        item.setRepeatState("NoRepeat");
        item.setUserName(username);
        model.addAttribute("newItem", item);                                                            //Defaults to a non repeating state.
        model.addAttribute("categoryList", categoryRepository.findByHasChildAndUserName(false, username));
        model.addAttribute("currentUser", username);
        return "createItem";
    }


    @PostMapping("/createItem")
    public String createItemSubmit(@CurrentSecurityContext(expression="authentication.name") String username, @ModelAttribute Item newItem, @RequestParam String dateString, Model model) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        cal.setTime(date);
        newItem.setExpiration(cal.getTimeInMillis());
        newItem.setExpirationState("Valid");
        newItem.setUserName(username);
        itemRepository.save(newItem);

        return "redirect:/";
    }

    @GetMapping("/createCategory")
    public String createCategoryForm(@CurrentSecurityContext(expression="authentication.name") String username, Model model) {
        Category newCategory = new Category();
        model.addAttribute("newCategory", newCategory);
        model.addAttribute("categoryList", categoryRepository.findByUserName(username));
        model.addAttribute("currentUser", username);
        return "createCategory";
    }

    @PostMapping("/createCategory")
    public String createCategorySubmit(@CurrentSecurityContext(expression="authentication.name") String username, Model model, @ModelAttribute("newCategory") Category newCategory) {
        Category parentCategory;
       if(!newCategory.getParentCategory().equals("Root")){

           parentCategory = categoryRepository.findByCategoryAndUserName(newCategory.getParentCategory(), username);
           if(!parentCategory.isHasChild()){
               List<Item> items = itemRepository.findByCategoryAndUserName(parentCategory.getCategory(), username);
               for (Item item:items){
                   item.setCategory(newCategory.getCategory());
                   itemRepository.save(item);
               }
               parentCategory.setHasChild(true);
               categoryRepository.save(parentCategory);
           }
       }
        newCategory.setUserName(username);
        newCategory.setExpirationState("Valid");
        newCategory.setHasChild(false);
        categoryRepository.save(newCategory);
        return "redirect:/";
    }

    @GetMapping("/list")
    public String categoryIndexForm(@CurrentSecurityContext(expression="authentication.name") String username, RedirectAttributes redirectAttributes, Model model,
                                    @RequestParam(defaultValue = "") String categoryName) {

       updateExpOfItems(username);
       if(categoryName.equals("Coming Up")){
           model.addAttribute("listOfItems", itemRepository.findByExpirationStateNotAndUserName("Valid", username));
       }
       else
       if(categoryName.equals(""))
           model.addAttribute("listOfItems", itemRepository.findByUserName(username));
       else
        model.addAttribute("listOfItems", itemRepository.findByCategoryAndUserName(categoryName, username));

       Category category = new Category();
       category.setCategory(categoryName);
       model.addAttribute("category", category);
        model.addAttribute("currentUser", username);
        return "dashboardList";
    }

    @PostMapping("/list")
    public String listIndexSubmit(@CurrentSecurityContext(expression="authentication.name") String username, @RequestParam int flag, @RequestParam(defaultValue = "") String categoryName , RedirectAttributes redirectAttributes,  @RequestParam Long id, Model model) {

        if(flag==0){                                                      //This executes if the user asks for deletion of the entry.
            itemRepository.deleteById(id);
        }
        else {                                                            //This executes if the user completes on entry.
            Item item = itemRepository.findById(id).orElseThrow();
            if (item.getRepeatState().equals("NoRepeat")) {
                itemRepository.deleteById(id);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(item.getExpiration());

                switch (item.getRepeatState()) {
                    case "Annually":
                        calendar.add(Calendar.YEAR, 1);
                        break;
                    case "Semi-Annually":
                        calendar.add(Calendar.MONTH, 6);
                        break;
                    case "Monthly":
                        calendar.add(Calendar.MONTH, 1);
                        break;
                }
                item.setExpiration(calendar.getTimeInMillis());
                itemRepository.save(item);
            }
        }
        model.addAttribute("listOfItems", itemRepository.findByUserName(username));
        redirectAttributes.addAttribute("categoryName", categoryName);
        return "redirect:list";
    }
    @GetMapping("/register")
    public String registrationForm(Model model){
       UserDTO user = new UserDTO();
       model.addAttribute("user", user);
       return "registration.html";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserDTO userDTO, Model model){
       System.out.println("Username: " + userDTO.getFirstName());
       userService.registerNewUserAccount(userDTO);
       return "redirect:/";
    }


    public void updateExpOfItems(String username){
       if(categoryRepository.existsByUserName(username)) {

           Calendar calendar = Calendar.getInstance();

           setApplicableCategoriesToValid(username);

           for (Item item : itemRepository.findByUserName(username)) {
               Calendar calendarItem = Calendar.getInstance();
               calendarItem.setTimeInMillis(item.getExpiration());


               if (calendarItem.before(calendar)) {
                   setItemAndCategoryTo(item, "Expired", username);
               } else {
                   int daysToWarn = item.getDaysToWarn();
                   calendar.add(Calendar.DATE, daysToWarn);
                   if (calendar.compareTo(calendarItem) > 0 /*&& !expired*/) {
                       setItemAndCategoryTo(item, "Warning", username);
                   }
                   calendar.add(Calendar.DATE, -daysToWarn);                    //Sets the calendar back to normal. We are in a loop, if this doesn't happen the changes build up and the calendar will not be accurate.
               }

               updateCategories(username);
           }
       }
    }

    public void setItemAndCategoryTo(Item item, String state, String username){

       item.setExpirationState(state);
       Category category = categoryRepository.findByCategoryAndUserName(item.getCategory(), username);
       if(!category.getExpirationState().equals("Expired")) {
           category.setExpirationState(state);
           categoryRepository.save(category);
       }
       itemRepository.save(item);
    }

    public void setApplicableCategoriesToValid(String username){

        for (Category category :categoryRepository.findByUserName(username)
        ) {
            if(!itemRepository.existsByCategoryAndUserNameAndExpirationState(category.getCategory(), username, "Expired") &&
                    !itemRepository.existsByCategoryAndUserNameAndExpirationState(category.getCategory(), username, "Warning")){

                category.setExpirationState("Valid");
            }
        }
    }

    public void updateCategories(String username){
        for (Category category: categoryRepository.findByHasChildAndUserName(false, username)) {
            if(!category.getExpirationState().equals("Valid")){
                updateParentTree(category, username);
            }

        }
    }

    public void updateParentTree(Category category, String username){
        if(!category.getParentCategory().equals("Root")){
            Category parentCategory = categoryRepository.findByCategoryAndUserName(category.getParentCategory(), username);
            if(!parentCategory.getExpirationState().equals("Expired"))
                parentCategory.setExpirationState(category.getExpirationState());
            categoryRepository.save(parentCategory);
            updateParentTree(parentCategory, username);
        }
    }


}