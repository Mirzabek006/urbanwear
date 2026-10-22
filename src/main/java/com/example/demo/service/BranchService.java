package com.example.demo.service;

import com.example.demo.model.Branch;
import com.example.demo.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getById(Long id) {
        return branchRepository.findById(id);
    }

    public void initBranchesIfEmpty() {
        if (branchRepository.count() == 0) {
            branchRepository.save(new Branch(
                    "Bosh Filial (Amir Temur)",
                    "Toshkent",
                    "Amir Temur shoh ko'chasi, 15-uy",
                    "+998 71 200 00 11",
                    "09:00 - 22:00 (Har kuni)",
                    41.311081,
                    69.279737,
                    "Amir Temur xiyoboni, O'zbekiston mehmonxonasi ro'parasi",
                    true
            ));

            branchRepository.save(new Branch(
                    "Chilonzor filiali (Integro)",
                    "Toshkent",
                    "Bunyodkor shoh ko'chasi, 52-uy",
                    "+998 71 200 00 22",
                    "10:00 - 22:00 (Har kuni)",
                    41.275812,
                    69.204523,
                    "Integro Savdo Markazi, 2-qavat (Chilonzor metrosi)",
                    false
            ));

            branchRepository.save(new Branch(
                    "Yunusobod filiali (Mega Planet)",
                    "Toshkent",
                    "Ahmad Donish ko'chasi, 2-B uy",
                    "+998 71 200 00 33",
                    "10:00 - 22:00 (Dam olishsiz)",
                    41.364952,
                    69.288219,
                    "Mega Planet Savdo Markazi, 1-qavat",
                    false
            ));

            branchRepository.save(new Branch(
                    "Samarqand Darvoza filiali",
                    "Toshkent",
                    "Qoratosh ko'chasi, 5-A uy",
                    "+998 71 200 00 44",
                    "10:00 - 23:00",
                    41.316412,
                    69.231145,
                    "Samarqand Darvoza KM, 3-qavat",
                    false
            ));

            branchRepository.save(new Branch(
                    "Beruniy filiali (Tinchlik)",
                    "Toshkent",
                    "Beruniy ko'chasi, 41-uy",
                    "+998 71 200 00 55",
                    "09:00 - 21:00",
                    41.345612,
                    69.205412,
                    "Tinchlik metrosi yaqinida",
                    false
            ));

            branchRepository.save(new Branch(
                    "Samarqand shahar filiali",
                    "Samarqand",
                    "Universitet xiyoboni, 12-uy",
                    "+998 66 233 44 55",
                    "09:00 - 21:00 (Har kuni)",
                    39.654300,
                    66.975800,
                    "Registon maydoni yaqinida",
                    false
            ));
        }
    }
}
