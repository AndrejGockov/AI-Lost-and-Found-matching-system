package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayConversationResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayMessageResponseDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayUserResponseDto;
import mk.finki.ukim.lost_and_found.service.application.MessageApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.web.csrf.CsrfToken;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/messages")
public class MessageController {
    private final MessageApplicationService messageApplicationService;
    private final UserService userService;

    // ---------- page ----------

    @GetMapping
    public String getMessagesPage(Principal principal, CsrfToken csrfToken, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        // Resolve the token now, before the response is committed
        model.addAttribute("csrfToken", csrfToken.getToken());
        model.addAttribute("csrfHeader", csrfToken.getHeaderName());
        return "messages/messages";
    }

    // ---------- JSON API used by the page's JavaScript ----------

    @PostMapping(value = "/api", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<DisplayMessageResponseDto> send(@RequestParam String receiverId,
                                                          @RequestParam(defaultValue = "") String content,
                                                          @RequestParam(required = false) List<MultipartFile> files,
                                                          Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageApplicationService.send(receiverId, content, files, user));
    }

    @GetMapping("/api/conversations")
    @ResponseBody
    public List<DisplayConversationResponseDto> conversations(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.findConversations(user);
    }

    @GetMapping("/api/conversations/{userId}")
    @ResponseBody
    public Page<DisplayMessageResponseDto> conversation(@PathVariable String userId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "30") int size,
                                                        Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.findConversation(userId, user, page, size);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<DisplayMessageResponseDto> findById(@PathVariable String id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.findById(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.deleteById(id, user)
                .map(m -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/attachments/{attachmentId}")
    @ResponseBody
    public ResponseEntity<byte[]> attachment(@PathVariable String attachmentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.findAttachmentById(attachmentId, user)
                .map(a -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(a.contentType()))
                        .contentLength(a.bytes().length)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"attachment-" + a.id() + "\"")
                        .header("X-Content-Type-Options", "nosniff")
                        .body(a.bytes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/users")
    @ResponseBody
    public List<DisplayUserResponseDto> searchUsers(@RequestParam(defaultValue = "") String q,
                                                    Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return messageApplicationService.searchUsers(q, user);
    }

    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<DisplayUserResponseDto> findUser(@PathVariable String id) {
        return messageApplicationService.findUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}