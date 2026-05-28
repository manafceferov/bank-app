package com.neobank.controller;

import com.neobank.dto.auth.LoginRequestDto;
import com.neobank.dto.auth.LoginResponseDto;
import com.neobank.dto.account.AccountCreateDto;
import com.neobank.dto.card.CardRequestDto;
import com.neobank.dto.deposit.DepositCreateDto;
import com.neobank.dto.credit.CreditApplyDto;
import com.neobank.dto.transaction.TransferRequestDto;
import com.neobank.dto.user.ChangePasswordDto;
import com.neobank.dto.user.UserEditDto;
import com.neobank.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MvcController {

    private final AuthService authService;
    private final AccountService accountService;
    private final CardService cardService;
    private final DepositService depositService;
    private final CreditService creditService;
    private final TransactionService transactionService;
    private final UserService userService;

    public MvcController(AuthService authService,
                         AccountService accountService,
                         CardService cardService,
                         DepositService depositService,
                         CreditService creditService,
                         TransactionService transactionService,
                         UserService userService
    ) {
        this.authService = authService;
        this.accountService = accountService;
        this.cardService = cardService;
        this.depositService = depositService;
        this.creditService = creditService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpServletRequest request,
                       Model model
    ) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model
    ) {
        try {
            LoginRequestDto dto = new LoginRequestDto();
            dto.setEmail(email);
            dto.setPassword(password);
            LoginResponseDto response = authService.login(dto).getData();
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("email", response.getEmail());
            session.setAttribute("role", response.getRole());
            session.setAttribute("token", response.getToken());
            session.setAttribute("firstName", response.getFirstName());
            session.setAttribute("lastName", response.getLastName());
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Email və ya şifrə yanlışdır");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String phoneNumber,
                           @RequestParam(required = false) String finCode,
                           Model model
    ) {
        try {
            var dto = new com.neobank.dto.auth.RegisterRequestDto();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setPhoneNumber(phoneNumber);
            dto.setFinCode(finCode);
            authService.register(dto);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model,
                            HttpServletRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
        model.addAttribute("credits", creditService.getMyCredits(userId).getData());
        model.addAttribute("deposits", depositService.getMyDepositsByUser(userId).getData());
        return "dashboard";
    }

    @GetMapping("/web/accounts")
    public String accounts(HttpSession session,
                           Model model,
                           HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
        model.addAttribute("cards", cardService.getMyCardsByUser(userId).getData());
        return "account/list";
    }

    @GetMapping("/web/accounts/new")
    public String newAccountPage(HttpSession session,
                                 Model model,
                                 HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        model.addAttribute("currentPath", request.getRequestURI());
        return "account/new";
    }

    @PostMapping("/web/accounts/new")
    public String createAccount(@RequestParam String accountType,
                                @RequestParam(defaultValue = "AZN") String currency,
                                HttpSession session,
                                Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            AccountCreateDto dto = new AccountCreateDto();
            dto.setAccountType(com.neobank.enums.AccountType.valueOf(accountType));
            dto.setCurrency(currency);
            accountService.create(userId, dto);
            return "redirect:/web/accounts";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPath", "/web/accounts/new");
            return "account/new";
        }
    }

    @GetMapping("/web/accounts/{id}")
    public String accountDetail(@PathVariable Long id,
                                HttpSession session,
                                Model model,
                                HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("account", accountService.getById(id, userId).getData());
        model.addAttribute("cards", cardService.getMyCards(id, userId).getData());
        model.addAttribute("deposits", depositService.getMyDeposits(id, userId).getData());
        model.addAttribute("transactions", transactionService.getHistory(id, userId, PageRequest.of(0, 20)).getData());
        return "account/detail";
    }

    @GetMapping("/web/profile")
    public String profilePage(HttpSession session,
                              Model model,
                              HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("user", userService.getById(userId).getData());
        return "user/profile";
    }

    @PostMapping("/web/profile/edit")
    public String editProfile(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam(required = false) String phoneNumber,
                              HttpSession session,
                              Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            UserEditDto dto = new UserEditDto();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setPhoneNumber(phoneNumber);
            userService.edit(userId, dto);
            session.setAttribute("firstName", firstName);
            session.setAttribute("lastName", lastName);
            model.addAttribute("success", "Məlumatlar yeniləndi");
            model.addAttribute("currentPath", "/web/profile");
            model.addAttribute("user", userService.getById(userId).getData());
            return "user/profile";
        } catch (Exception e) {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPath", "/web/profile");
            model.addAttribute("user", userService.getById(userId).getData());
            return "user/profile";
        }
    }

    @PostMapping("/web/profile/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            ChangePasswordDto dto = new ChangePasswordDto();
            dto.setCurrentPassword(currentPassword);
            dto.setNewPassword(newPassword);
            userService.changePassword(userId, dto);
            model.addAttribute("successPassword", "Şifrə dəyişdirildi");
            model.addAttribute("currentPath", "/web/profile");
            model.addAttribute("user", userService.getById(userId).getData());
            return "user/profile";
        } catch (Exception e) {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("errorPassword", e.getMessage());
            model.addAttribute("currentPath", "/web/profile");
            model.addAttribute("user", userService.getById(userId).getData());
            return "user/profile";
        }
    }

    @GetMapping("/web/cards/request/{accountId}")
    public String requestCardPage(@PathVariable Long accountId,
                                  HttpSession session,
                                  Model model,
                                  HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("accountId", accountId);
        return "card/request";
    }

    @PostMapping("/web/cards/request")
    public String requestCard(@RequestParam Long accountId,
                              @RequestParam String cardType,
                              HttpSession session,
                              Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            CardRequestDto dto = new CardRequestDto();
            dto.setAccountId(accountId);
            dto.setCardType(com.neobank.enums.CardType.valueOf(cardType));
            cardService.requestCard(userId, dto);
            return "redirect:/web/accounts/" + accountId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("accountId", accountId);
            model.addAttribute("currentPath", "/web/cards/request/" + accountId);
            return "card/request";
        }
    }

    @PostMapping("/web/cards/{cardId}/block")
    public String blockCard(@PathVariable Long cardId,
                            @RequestParam Long accountId,
                            HttpSession session
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        cardService.blockCard(cardId, userId);
        return "redirect:/web/accounts/" + accountId;
    }

    @GetMapping("/web/transactions/transfer")
    public String transferPage(HttpSession session,
                               Model model,
                               HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
        return "transaction/transfer";
    }

    @PostMapping("/web/transactions/transfer")
    public String transfer(@RequestParam Long fromAccountId,
                           @RequestParam(required = false) String toIban,
                           @RequestParam(required = false) String toCardNumber,
                           @RequestParam java.math.BigDecimal amount,
                           @RequestParam(required = false) String description,
                           HttpSession session,
                           Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            TransferRequestDto dto = new TransferRequestDto();
            dto.setFromAccountId(fromAccountId);
            dto.setToIban(toIban);
            dto.setToCardNumber(toCardNumber);
            dto.setAmount(amount);
            dto.setDescription(description);
            transactionService.transfer(userId, dto);
            return "redirect:/web/accounts/" + fromAccountId;
        } catch (Exception e) {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPath", "/web/transactions/transfer");
            model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
            return "transaction/transfer";
        }
    }

    @GetMapping("/web/deposits")
    public String deposits(HttpSession session,
                           Model model,
                           HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null)
            return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("deposits", depositService.getMyDepositsByUser(userId).getData()
        );
        return "deposit/list";
    }

    @GetMapping("/web/deposits/new/{accountId}")
    public String newDepositPage(@PathVariable Long accountId,
                                 HttpSession session,
                                 Model model,
                                 HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("account", accountService.getById(accountId, userId).getData());
        return "deposit/new";
    }

    @PostMapping("/web/deposits/new")
    public String createDeposit(@RequestParam Long accountId,
                                @RequestParam java.math.BigDecimal amount,
                                @RequestParam Integer durationMonths,
                                HttpSession session,
                                Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            DepositCreateDto dto = new DepositCreateDto();
            dto.setAccountId(accountId);
            dto.setAmount(amount);
            dto.setDurationMonths(durationMonths);
            depositService.create(userId, dto);
            return "redirect:/web/accounts/" + accountId;
        } catch (Exception e) {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPath", "/web/deposits/new/" + accountId);
            model.addAttribute("account", accountService.getById(accountId, userId).getData());
            return "deposit/new";
        }
    }

    @PostMapping("/web/deposits/{depositId}/close")
    public String closeDeposit(@PathVariable Long depositId,
                               @RequestParam Long accountId,
                               HttpSession session
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        depositService.close(depositId, userId);
        return "redirect:/web/accounts/" + accountId;
    }


    @GetMapping("/web/credits")
    public String credits(HttpSession session,
                          Model model,
                          HttpServletRequest request) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("credits", creditService.getMyCredits(userId).getData());
        return "credit/list";
    }

    @GetMapping("/web/credits/apply")
    public String applyCreditPage(HttpSession session,
                                  Model model,
                                  HttpServletRequest request
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
        return "credit/apply";
    }

    @PostMapping("/web/credits/apply")
    public String applyCredit(@RequestParam Long accountId,
                              @RequestParam java.math.BigDecimal amount,
                              @RequestParam Integer durationMonths,
                              HttpSession session,
                              Model model
    ) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        try {
            Long userId = (Long) session.getAttribute("userId");
            CreditApplyDto dto = new CreditApplyDto();
            dto.setAccountId(accountId);
            dto.setAmount(amount);
            dto.setDurationMonths(durationMonths);
            creditService.apply(userId, dto);
            return "redirect:/web/credits";
        } catch (Exception e) {
            Long userId = (Long) session.getAttribute("userId");
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentPath", "/web/credits/apply");
            model.addAttribute("accounts", accountService.getMyAccounts(userId).getData());
            return "credit/apply";
        }
    }
}