package com.team1.animalproject.view;

import com.team1.animalproject.model.Cins;
import com.team1.animalproject.model.Tur;
import com.team1.animalproject.service.CinsService;
import com.team1.animalproject.service.TurService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope ("view")
@EqualsAndHashCode ()
@Data
public class CinsBean extends BaseViewController<Cins> implements Serializable {

	private static final long serialVersionUID = -4784862612385542516L;

	private static final Logger logger = LoggerFactory.getLogger(CinsBean.class);

	@Autowired
	private CinsService cinsService;

	@Autowired
	private TurService turService;

	private Cins cins = new Cins();
	private List<Cins> selectedCinss;
	private List<Cins> allCinss;
	private List<Cins> filteredCinss;
	private List<Tur> turler;

	private boolean showCreateOrEdit;
	private boolean showInfo;

	@Override
	@PostConstruct
	public void viewOlustur() {
		super.altVerileriVeIlkEkraniHazirla();
		allCinss = cinsService.ara();
		filteredCinss = new ArrayList<>(allCinss);
	}

	@Override
	public void ilkEkraniHazirla() {
		showCreateOrEdit = false;
		showInfo = false;
		allCinss = cinsService.ara();
		filteredCinss = new ArrayList<>(allCinss);
		turler = turService.getAll();
		cins = new Cins();
	}

	public void kaydet() throws IOException {
		cinsService.save(cins);
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Başarılı", "Cins verisi başarıyla işlem görmüştür."));
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesContext.getCurrentInstance().getExternalContext().redirect("/cins/cins.jsf");

	}

	@SuppressWarnings ("OptionalGetWithoutIsPresent")
	public void kullanimaAl() throws IOException {
		cins = selectedCinss.stream().findFirst().get();
		cins.setDurum(true);
		kaydet();
	}

	public boolean kullanimdaVarmi() {
		return selectedCinss.stream().anyMatch(Cins::isDurum);
	}

	public void prepareNewScreen() {
		showCreateOrEdit = true;
	}

	@SuppressWarnings ("OptionalGetWithoutIsPresent")
	public void prepareUpdateScreen() {
		cins = selectedCinss.stream().findFirst().get();
		showCreateOrEdit = true;
	}

	@SuppressWarnings ("OptionalGetWithoutIsPresent")
	public void prepareInfoScreen() {
		cins = selectedCinss.stream().findFirst().get();
		showCreateOrEdit = true;
		showInfo = true;
	}

	public void sil() throws IOException {
		cinsService.delete(selectedCinss);
		FacesContext.getCurrentInstance().getExternalContext().redirect("/cins/cins.jsf");
	}

}
