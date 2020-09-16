package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import com.example.servingwebcontent.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Controller
public class DashboardController {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
   @GetMapping("/")
    public String dashboardIndexForm(RedirectAttributes redirectAttributes, Model model, @RequestParam(defaultValue = "") String categoryName) {
        updateExpOfItems();
        if(categoryName.equals("")) {

            model.addAttribute("listOfCategories", categoryRepository.findByParentCategory("Root"));
       }
        else {
            if(categoryRepository.findByCategory(categoryName).isHasChild()) {

                model.addAttribute("listOfCategories", categoryRepository.findByParentCategory(categoryName));
            }
            else {

                redirectAttributes.addAttribute("categoryName", categoryName);
                return "redirect:/list";
            }
        }

        return "index.html";
    }

    @GetMapping("/create")
    public String createItemForm(Model model) {
        Item item = new Item();
        item.setRepeatState("NoRepeat");
        model.addAttribute("newItem", item);                                                            //Defaults to a non repeating state.
        model.addAttribute("categoryList", categoryRepository.findByHasChild(false));

        return "createItem";
    }


    @PostMapping("/create")
    public String createItemSubmit(@ModelAttribute Item newItem, @RequestParam String dateString, Model model) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        cal.setTime(date);
        newItem.setExpiration(cal.getTimeInMillis());
        newItem.setExpirationState("Valid");
        itemRepository.save(newItem);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String categoryIndexForm(RedirectAttributes redirectAttributes, Model model, @RequestParam(defaultValue = "") String categoryName) {
       updateExpOfItems();
       if(categoryName.equals("Coming Up")){
           model.addAttribute("listOfItems", itemRepository.findByExpirationStateNot("Valid"));
       }
       else
       if(categoryName.equals(""))
           model.addAttribute("listOfItems", itemRepository.findAll());
       else
        model.addAttribute("listOfItems", itemRepository.findByCategory(categoryName));

       Category category = new Category();
       category.setCategory(categoryName);
       model.addAttribute("category", category);
        return "dashboardList";
    }

    @PostMapping("/list")
    public String listIndexSubmit(@RequestParam int flag, @RequestParam(defaultValue = "") String categoryName , RedirectAttributes redirectAttributes,  @RequestParam Long id, Model model) {

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
        model.addAttribute("listOfItems", itemRepository.findAll());
        redirectAttributes.addAttribute("categoryName", categoryName);
        return "redirect:list";
    }

    public void updateExpOfItems(){
       Calendar calendar = Calendar.getInstance();

        setApplicableCategoriesToValid();

        for (Item item: itemRepository.findAll()){
            Calendar calendarItem = Calendar.getInstance();
            calendarItem.setTimeInMillis(item.getExpiration());
            if(calendarItem.before(calendar)) {
                setItemAndCategoryTo(item, "Expired");
            }
             else{
                 int daysToWarn = item.getDaysToWarn();
                 calendar.add(Calendar.DATE,daysToWarn);
                 if(calendar.compareTo(calendarItem) > 0){
                     setItemAndCategoryTo(item, "Warning");
                 }
                 else
                     setItemAndCategoryTo(item, "Valid");
                calendar.add(Calendar.DATE,-daysToWarn);                    //Sets the calendar back to normal. We are in a loop, if this doesn't happen the changes build up and the calendar will not be accurate.
             }

            updateCategories();
        }
    }

    public void setItemAndCategoryTo(Item item, String state){

       item.setExpirationState(state);
       Category category = categoryRepository.findByCategory(item.getCategory());
       category.setExpirationState(state);

       categoryRepository.save(category);
       itemRepository.save(item);
    }

    public void setApplicableCategoriesToValid(){

        for (Category category :categoryRepository.findAll()
        ) {
            if(!itemRepository.existsByCategory(category.getCategory())){
                category.setExpirationState("Valid");
            }
        }
    }

    public void updateCategories(){
        for (Category category: categoryRepository.findByHasChild(false)) {
            if(!category.getExpirationState().equals("Valid")){
                updateParentTree(category);
            }

        }
    }

    public void updateParentTree(Category category){
        if(!category.getParentCategory().equals("Root")){
            Category parentCategory = categoryRepository.findByCategory(category.getParentCategory());
            if(!parentCategory.getExpirationState().equals("Expired"))
                parentCategory.setExpirationState(category.getExpirationState());
            categoryRepository.save(parentCategory);
            updateParentTree(parentCategory);
        }
    }

}