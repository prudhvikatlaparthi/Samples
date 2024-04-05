package com.pru.lib.exam;

class InvalidEmailException extends Exception {
    public InvalidEmailException(String message) {
        super(message);
    }
}

class InvalidPasswordException extends Exception {
    public InvalidPasswordException(String message) {
        super(message);
    }
}

class PasswordNotMatchException extends Exception {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
public class Register {

    public String checkCredentials(String email, String pass, String cpass) throws InvalidEmailException,
            InvalidPasswordException, PasswordNotMatchException {
        if (!(email.contains("@") && email.contains("."))){
            throw new InvalidEmailException("Invalid Email");
        }
        if (pass.length() < 6){
            throw new InvalidPasswordException("Invalid Password");
        }

        if (!pass.equals(cpass)){
            throw new PasswordNotMatchException("Password not match");
        }

        return "Registered";
    }

    public static void main(String[] args){
        Register register = new Register();
        try {
            System.out.println(register.checkCredentials("sdfsfs@fgd.dg","123456dfg","123456dfg"));
        } catch (InvalidEmailException | InvalidPasswordException | PasswordNotMatchException e) {
            e.printStackTrace();
        }
    }
}
