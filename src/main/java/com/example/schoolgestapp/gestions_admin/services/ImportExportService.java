package com.example.schoolgestapp.gestions_admin.services;

import com.example.schoolgestapp.gestions_users.dto.RegisterRequest;
import com.example.schoolgestapp.gestions_users.dto.UserResponseDTO;
import com.example.schoolgestapp.gestions_users.services.Authservice;
import com.opencsv.CSVReader;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SERVICE : Importation et Exportation de données.
 * Gère la conversion des fichiers CSV en entités système.
 */
@Service
public class ImportExportService {

    private final Authservice authservice;

    public ImportExportService(Authservice authservice) {
        this.authservice = authservice;
    }

    /**
     * Importe des utilisateurs à partir d'un fichier CSV.
     * Format attendu : username,password,email,role
     */
    public List<Map<String, Object>> importUsersFromCsv(MultipartFile file) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            
            // Sauter l'en-tête si présent
            csvReader.readNext(); 

            while ((line = csvReader.readNext()) != null) {
                if (line.length < 4) continue;

                RegisterRequest request = new RegisterRequest();
                request.setUsername(line[0].trim());
                request.setPassword(line[1].trim());
                request.setEmail(line[2].trim());
                request.setRole(line[3].trim().toUpperCase());

                try {
                    results.add(java.util.Map.of("success", authservice.register(request)));
                } catch (Exception e) {
                    results.add(java.util.Map.of("error", "Erreur ligne " + line[0] + ": " + e.getMessage()));
                }
            }
        }
        return results;
    }

    /**
     * Génère un contenu CSV simple pour exporter les utilisateurs.
     */
    public String exportUsersToCsv(List<UserResponseDTO> users) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Username;Email;Role;Status\n");

        for (UserResponseDTO u : users) {
            csv.append(u.getId()).append(";")
               .append(u.getUsername()).append(";")
               .append(u.getEmail()).append(";")
               .append(u.getRole()).append(";")
               .append(u.isActive() ? "ACTIF" : "INACTIF").append("\n");
        }
        return csv.toString();
    }
}
