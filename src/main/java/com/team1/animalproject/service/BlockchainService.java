package com.team1.animalproject.service;

import com.google.gson.Gson;
import com.team1.animalproject.auth.Constants;
import com.team1.animalproject.model.Kullanici;
import com.team1.animalproject.model.MedicalReport;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class BlockchainService {

    private List<String> hashes;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserService userService;

    public void init() throws IOException {
        File newFile = new File(Constants.FILE_PATH + "authority.achain");
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
    }

    public void kullaniciDosyasiOlustur(String userId) throws IOException {
        File newFile = new File(Constants.FILE_PATH + userId + ".achain");
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
    }

    public void dosyayiGuncelHaleGetir(String userId) throws IOException {

        String json = null;
        File newFile = new File(Constants.FILE_PATH + userId + ".achain");

        hashes = new ArrayList<>();


        FileWriter writer = new FileWriter(newFile);
        for (String str : hashes) {
            writer.write(str + "\n");
        }
        writer.close();
    }

    public boolean validate(String userId) {
        List<String> userHashes = readFile(userId);
        List<String> authorityHashes = readFile("authority");
        for (int i = 0; i < userHashes.size(); i++) {
            if (!authorityHashes.contains(userHashes.get(0))) {
                return false;
            }
        }
        return true;
    }


    public List<String> readFile(String userId) {
        List<String> hashlist = new ArrayList<>();
        try {
            File myObj = new File(Constants.FILE_PATH + userId + ".achain");
            Scanner myReader = new Scanner(myObj);
            myReader.useDelimiter("/nextBlock/");
            while (myReader.hasNext()) {
                String data = myReader.next();
                hashlist.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return hashlist;
    }

    public List<MedicalReport> getAll(String userId) {
        List<String> authorityHashes = readFile(userId);
        List<MedicalReport> medicalReports = new ArrayList<>();
        authorityHashes.stream().forEach(s -> {
            String jsonObjects = new String(Base64.getDecoder().decode(s.getBytes()));
            JSONObject jsonObject = new JSONObject(jsonObjects);
            String id = jsonObject.getString("olusturan");
            Kullanici kullanici = userService.findById(id).get();
            String olusturan = kullanici.getName() + " " + kullanici.getSurname();
            medicalReports.add(MedicalReport.builder()
                    .id(jsonObject.getString("id"))
                    .date(jsonObject.getString("date"))
                    .tension(jsonObject.getString("tension"))
                    .animalId(jsonObject.getString("animalId"))
                    .medicines(jsonObject.getString("medicines"))
                    .olusturan(olusturan)
                    .surgeryDescription(jsonObject.getString("surgeryDescription"))
                    .description(jsonObject.getString("description"))
                    .reportNum(jsonObject.getString("reportNum"))
                    .weight(jsonObject.getString("weight"))
                    .pulse(jsonObject.getString("pulse"))
                    .bodySugarLevel(jsonObject.getString("bodySugarLevel"))
                    .build());
        });
        return medicalReports;
    }

    public List<MedicalReport> getAllByAnimalId(String animalId) {
        List<String> authorityHashes = readFile("authority");
        List<MedicalReport> medicalReports = new ArrayList<>();
        authorityHashes.stream().forEach(s -> {
            String jsonObjects = new String(Base64.getDecoder().decode(s.getBytes()));
            JSONObject jsonObject = new JSONObject(jsonObjects);
            if (jsonObject.getString("animalId").equals(animalId)) {
                String id = jsonObject.getString("olusturan");
                Kullanici kullanici = userService.findById(id).get();
                String olusturan = kullanici.getName() + " " + kullanici.getSurname();
                medicalReports.add(MedicalReport.builder()
                        .id(jsonObject.getString("id"))
                        .date(jsonObject.getString("date"))
                        .tension(jsonObject.getString("tension"))
                        .animalId(jsonObject.getString("animalId"))
                        .medicines(jsonObject.getString("medicines"))
                        .olusturan(olusturan)
                        .surgeryDescription(jsonObject.getString("surgeryDescription"))
                        .description(jsonObject.getString("description"))
                        .reportNum(jsonObject.getString("reportNum"))
                        .weight(jsonObject.getString("weight"))
                        .pulse(jsonObject.getString("pulse"))
                        .bodySugarLevel(jsonObject.getString("bodySugarLevel"))
                        .build());
            }
        });
        return medicalReports;
    }

    public void transactionOlustur(MedicalReport medicalReport) throws IOException {
        List<MedicalReport> all = getAll("authority");
        medicalReport.setId(UUID.randomUUID().toString());
        medicalReport.setReportNum(all.size() + 1 + "");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(medicalReport);
        String encoded = Base64.getEncoder().encodeToString(jsonStr.getBytes());
        uzerineYaz(encoded, medicalReport.getOlusturan());
    }

    public void uzerineYaz(String data, String olusturan) throws IOException {
        File file = new File(Constants.FILE_PATH + "authority.achain");
        FileWriter fr = new FileWriter(file, true);
        fr.write(data + "/nextBlock/");
        fr.close();

        copyFileUsingStream(new File(Constants.FILE_PATH + "authority.achain"), new File(Constants.FILE_PATH + olusturan + ".achain"));
    }


    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
}
