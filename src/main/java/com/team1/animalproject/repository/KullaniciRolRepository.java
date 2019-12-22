package com.team1.animalproject.repository;

import com.team1.animalproject.model.KullaniciRol;
import com.team1.animalproject.model.ShelterWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("kullaniciRolRepository")
public interface KullaniciRolRepository extends JpaRepository<KullaniciRol, String> {

    List<KullaniciRol> findByKullaniciId(String kullaniciId);

    List<KullaniciRol> findByRolId(String rolId);

    void deleteByKullaniciId(String kullaniciId);

    void deleteByRolId(String rolId);

}