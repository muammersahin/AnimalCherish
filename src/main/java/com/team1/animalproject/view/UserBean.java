package com.team1.animalproject.view;

import com.team1.animalproject.auth.Constants;
import com.team1.animalproject.exception.BaseExceptionType;
import com.team1.animalproject.model.Kullanici;
import com.team1.animalproject.model.dto.KullaniciPrincipal;
import com.team1.animalproject.service.UserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Component
@Scope("view")
@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class UserBean extends BaseViewController<Kullanici> implements Serializable {

	@Autowired
	private UserService userService;

	private static final long serialVersionUID = 5246560358820611506L;

	private static final Logger logger = LoggerFactory.getLogger(UserBean.class);

	private Kullanici kullanici = new Kullanici();

	@Override
	@PostConstruct
	public void viewOlustur() {
		super.altVerileriVeIlkEkraniHazirla();
	}

	@Override
	public void ilkEkraniHazirla() {
	}

	public void kayitOl() throws IOException {
		boolean kayit = userService.kayitOl(kullanici);
		if(kayit){
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Başarılı",  "Başarıyla kaydolundu.") );
			context.getExternalContext().getFlash().setKeepMessages(true);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/login.jsf");
		}else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, BaseExceptionType.KULLANICI_ADI_MAIL_PHONE_KULLANILIYOR.getValidationMessage(), null) );
			context.getExternalContext().getFlash().setKeepMessages(true);
		}
	}

	public void login() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext extenalContext = facesContext.getExternalContext();
		RequestDispatcher dispatcher = ((ServletRequest) extenalContext.getRequest()).getRequestDispatcher(Constants.LOGIN_PROCESSING_URL);
		try{
			dispatcher.forward((ServletRequest) extenalContext.getRequest(), (ServletResponse) extenalContext.getResponse());
		} catch (ServletException | IOException e){
			log.error("Couldnt forward request to RequestDispatcher ", e);
		}
		facesContext.responseComplete();
	}


}
