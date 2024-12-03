package com.example.loanapi.service.impl;

import com.example.loanapi.constant.Role;
import com.example.loanapi.model.UserModel;
import com.example.loanapi.repository.LoanRepository;
import com.example.loanapi.repository.UserRepository;
import com.example.loanapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {


    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public UserModel getCredentials() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return (UserModel) authentication.getPrincipal();
    }

    public boolean checkValidForCustomer(Long customerId) {
        return customerId.equals(getCredentials().getId());
    }

    public boolean checkLoanExistForCustomer(Long loanId) {
        UserModel userModel = getCredentials();
        if(userModel.getRole().equals(Role.ROLE_CUSTOMER)) {
            return loanRepository.existsByIdAndCustomer_Id(loanId, userModel.getId());
        }
        return false;
    }
}
