package com.app.service.validator;

import com.app.persistence.dto.CreateUserDto;
import com.app.service.validator.generic.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateUserDtoValidator implements Validator<CreateUserDto> {
    @Override
    public Map<String, String> validate(CreateUserDto createUserDto) {
        var errors = new HashMap<String, String>();

        if(Objects.isNull(createUserDto)){
            errors.put("object", "is null");
            return errors;
        }

        String nameValidation = validateNameOrSurname(createUserDto.getName());
        if(!nameValidation.isEmpty()){
            errors.put("name", nameValidation);
        }

        String surnameValidation = validateNameOrSurname(createUserDto.getSurname());
        if(!surnameValidation.isEmpty()){
            errors.put("surname", surnameValidation);
        }

        String usernameValidation = validateUsername(createUserDto.getUsername());
        if(!usernameValidation.isEmpty()){
            errors.put("username", usernameValidation);
        }

        String passwordValidation = validatePassword(createUserDto.getPassword());
        if(!passwordValidation.isEmpty()){
            errors.put("password", passwordValidation);
        }

        String emailValidation = validateEmail(createUserDto.getEmail());
        if(!emailValidation.isEmpty()){
            errors.put("email", emailValidation);
        }

        if(!createUserDto.getEmail().equals(createUserDto.getEmailConfirmation())){
            errors.put("email confirmation", "not equal");
        }

        if(!createUserDto.getPassword().equals(createUserDto.getPasswordConfirmation())){
            errors.put("password confirmation", "not equal");
        }

        return errors;
    }

    private String validateNameOrSurname(String name){
        if(Objects.isNull(name)){
            return "is null!";
        }
        if(!name.matches("[A-Z][a-z]+")){
            return "does not match to regular expression";
        }

        return "";
    }

    private String validateUsername(String username){
        if(Objects.isNull(username)){
            return "is null!";
        }
        if(!username.matches("[a-zA-Z1-9]{8,}")){
            return "does not match to regular expression";
        }
        return "";
    }

    private String validatePassword(String password){
        if(Objects.isNull(password)){
            return "is null!";
        }
        if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\+\\-\\=])(?=.*[A-Z])(?!.*\\s).{8,}$")){
            return "does not match to regular expression";
        }
        return "";
    }

    private String validateEmail(String email){
        if(Objects.isNull(email)){
            return "is null!";
        }
        if(!email.matches("[a-zA-z1-9\\_\\.\\-]+@.+\\.[a-z]+")){
            return "does not match to regular expression";
        }
        return "";
    }

}
