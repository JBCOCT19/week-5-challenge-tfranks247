package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listJobs(Model model){
        model.addAttribute("jobs", jobRepository.findAll());
        return "list";

    }

    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search")String search){
        model.addAttribute("jobs", jobRepository.findByJobTitleContainingIgnoreCaseOrJobDescriptionContainingIgnoreCaseOrPostedDateContainingOrAuthorContainingOrPhoneNumberContainingIgnoreCase(search, search, search, search, search));
                return "searchlist";
    }

    @GetMapping("/add")
    public String jobForm(Model model){
        model.addAttribute("job", new Job());
        return "jobform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Job job1, @Valid Job job, BindingResult result,
                              @RequestParam("file")MultipartFile file
                              ){
        if(result.hasErrors()){
            return "jobform";
        }
        if (file.isEmpty() && job1.getImage() == null){
            return "redirect:/add";
        }
        if(!file.isEmpty()){
            try{
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                job.setImage(uploadResult.get("url").toString());
                jobRepository.save(job);
            }catch (IOException e){
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else
            jobRepository.save(job);
        return "redirect:/";
    }

    @RequestMapping("/list/{id}")
    public String showJob(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("job", jobRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateJob(@PathVariable("id") long id, Model model){
        model.addAttribute("job", jobRepository.findById(id).get());
        return "jobform";
    }
    @RequestMapping("/delete/{id}")
    public String delJob(@PathVariable("id")long id){
        jobRepository.deleteById(id);
        return "redirect:/";
    }
}
