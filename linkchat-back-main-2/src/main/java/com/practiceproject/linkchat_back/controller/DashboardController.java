package com.practiceproject.linkchat_back.controller;

import com.practiceproject.linkchat_back.model.Chat;
import com.practiceproject.linkchat_back.model.VitaliiSettings;
import com.practiceproject.linkchat_back.repository.ChatRepository;
import com.practiceproject.linkchat_back.repository.UserRepository;
import com.practiceproject.linkchat_back.repository.ChatMessageRepository;
//import com.practiceproject.linkchat_back.model.Setting1;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private String message = "";
    private String chatCreationResult = "";
    private List<VitaliiSettings> settings = new ArrayList<>();
    //private List<Setting1> settings = new ArrayList<>();
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public DashboardController(ChatRepository chatRepository, UserRepository userRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/ui/dashboard")
    public String dashboard(Model model) {
        try {
            long totalUsers = userRepository.count();
            List<Chat> allChats = chatRepository.findAll();
            long totalChats = allChats.size();
            long totalMessages = chatMessageRepository.count();

            model.addAttribute("message", message);
            model.addAttribute("chatResult", chatCreationResult);
            model.addAttribute("settings", settings);
            model.addAttribute("chats", allChats);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalChats", totalChats);
            model.addAttribute("totalMessages", totalMessages);
            return "dashboard";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "A system error occurred. Please try again later or contact support.");
            return "maintenance";
        }
    }

    @PostMapping("/ui/dashboard")
    public String dashboard(@RequestParam String newMessage) {
        this.message = newMessage;
        return "redirect:/ui/dashboard";
    }

    @PostMapping("/ui/dashboard/create")
    public String createChat(@RequestParam("chatTitle") String chatTitle, RedirectAttributes redirectAttributes) {
        if (chatTitle == null || chatTitle.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Chat title is required");
            return "redirect:/ui/dashboard";
        }
        try {
            String link = generateRandomLink(6);
            Chat chat = new Chat();
            chat.setTitle(chatTitle);
            chat.setLink(link);
            chatRepository.save(chat);
            this.chatCreationResult = "Chat created successfully with link: " + link;
        } catch (Exception ex) {
            this.chatCreationResult = "Error creating chat: " + ex.getMessage();
        }
        return "redirect:/ui/dashboard";
    }

    @PostMapping("/ui/dashboard/settings")
    public String saveSettings(@RequestParam("settingName") String name,
                              @RequestParam("settingValue") String value,
                               @RequestParam("settingType") String type) {

        VitaliiSettings setting = new VitaliiSettings(name, value, type);
        //Setting1 setting = new Setting1(name, value);
        settings.add(setting);
        return "redirect:/ui/dashboard";
    }

    private String generateRandomLink(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


}
