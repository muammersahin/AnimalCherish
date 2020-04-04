package com.team1.animalproject.integrationtest;

import com.team1.animalproject.model.Animal;
import com.team1.animalproject.preparer.AnimalPreparer;
import com.team1.animalproject.repository.AnimalRepository;
import com.team1.animalproject.view.utils.DateUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@EnableAutoConfiguration
@TestPropertySource (locations = "classpath:/application-test.properties")
@SpringBootTest
public class AnimalRepositoryIT  extends AbstractTestNGSpringContextTests {

	@Autowired
	private AnimalRepository animalRepository;

	@Test
	public void ara() {
		for(int i = 0; i < 10; i++){
			animalRepository.save(AnimalPreparer.olustur());
		}

		List<Animal> animals = animalRepository.animalAra();
		Assert.assertTrue(animals.size() == 10);
		animalRepository.deleteAll();
	}

	@Test
	public void getAll() {
		for(int i = 0; i < 5; i++){
			animalRepository.save(AnimalPreparer.olustur());
		}

		List<Animal> animals = animalRepository.findAll();
		Assert.assertTrue(animals.size() == 5);
		animalRepository.deleteAll();
	}

	@Test
	public void save() {
		Animal animal = AnimalPreparer.olustur();
		animalRepository.saveAndFlush(animal);

		List<String> animalIds = Lists.newArrayList();
		animalIds.add(animal.getId());

		Animal savedAnimal = animalRepository.findByIdIn(animalIds).get().get(0);
		Assert.assertTrue(savedAnimal.equals(animal));
		animalRepository.deleteAll();
	}

	@Test
	public void update() {
		Animal animal = AnimalPreparer.olustur();
		animalRepository.saveAndFlush(animal);

		List<String> animalIds = Lists.newArrayList();
		animalIds.add(animal.getId());

		Animal savedAnimal = animalRepository.findByIdIn(animalIds).get().get(0);

		savedAnimal.setName(RandomStringUtils.randomAlphabetic(30));
		animalRepository.save(savedAnimal);

		animalIds = Lists.newArrayList();
		animalIds.add(savedAnimal.getId());

		Animal updatedAnimal = animalRepository.findByIdIn(animalIds).get().get(0);

		Assert.assertFalse(updatedAnimal.getName().equals(animal.getName()));
		animalRepository.deleteAll();
	}

	@Test
	public void delete() {
		Animal animal = AnimalPreparer.olustur();
		animalRepository.saveAndFlush(animal);

		List<String> animalIds = Lists.newArrayList();
		animalIds.add(animal.getId());

		Animal savedAnimal = animalRepository.findByIdIn(animalIds).get().get(0);

		animalRepository.delete(savedAnimal);

		animalIds = Lists.newArrayList();
		animalIds.add(animal.getId());

		Optional<List<Animal>> optionalAnimal = animalRepository.findByIdIn(animalIds);

		Assert.assertFalse(optionalAnimal.isPresent());
		animalRepository.deleteAll();
	}

	@Test
	public void findByIdIn() {
		for(int i = 0; i < 5; i++){
			animalRepository.save(AnimalPreparer.olustur());
		}

		List<Animal> animals = animalRepository.findAll();

		List<String> ids = animals.stream().map(Animal::getId).collect(Collectors.toList());

		List<Animal> saveds = animalRepository.findByIdIn(ids).get();

		Assert.assertTrue(saveds.size() == 5);
		animalRepository.deleteAll();
	}

	@Test
	public void sonYediGunIcinEklenenHayvanVerileriniGetir() {
		for(int i = 0; i < 5; i++){
			Animal olusturulan = AnimalPreparer.olustur();
			olusturulan.setOlusmaTarihi(DateUtil.minusDays(DateUtil.nowAsDate(), i));
			animalRepository.save(olusturulan);
		}

		List<Animal> animals = animalRepository.findAll();
		Assert.assertTrue(animals.size() == 5);

		Map<Integer, Long> yediGun = animalRepository.sonYediGunIcinEklenenHayvanVerileriniGetir();

		Assert.assertTrue(yediGun.keySet().size() == 5);
		animalRepository.deleteAll();
	}

}
