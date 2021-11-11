package com.eprescription.eprescription.controller;

import com.eprescription.eprescription.entity.Doctor;
import com.eprescription.eprescription.entity.Drug;
import com.eprescription.eprescription.entity.Patient;
import com.eprescription.eprescription.service.DrugService;
import com.eprescription.eprescription.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PatientController {

    private final PatientService patientService;
    private final DrugService drugService;

    public PatientController(@Autowired PatientService patientService,
                             @Autowired DrugService drugService) {
        this.patientService = patientService;
        this.drugService = drugService;
    }

    @GetMapping("/newPatient")
    public ModelAndView register() {
        ModelAndView mav = new ModelAndView("patientForm");
        mav.addObject("patient", new Patient());
        mav.addObject("drugs", drugService.findAllDrugs());
        return mav;
    }

    @PostMapping("/patient/save")
    public String createPatient(Patient patient,
                                @ModelAttribute(value = "drugs") Drug drug) {
        drug = drugService.findDrugById(drug.getId());
        if(patient.getPatientsDrugs() == null) {
            patient.setPatientsDrugs(new ArrayList<Drug>());
        }
        patient.getPatientsDrugs().add(drug);
        patientService.savePatient(patient);
        return "redirect:/allPatients";
    }

    @GetMapping("/allPatients")
    public String getAllPatients(Model model) {
        List<Patient> patientList = patientService.findAllPatients();
        model.addAttribute("patientList", patientList);
        return "patients";
    }

    @GetMapping("/editPatient/{id}")
    public ModelAndView editPatient(@PathVariable("id") String patientId) {
        ModelAndView mav = new ModelAndView("patientformEdit");
        Long pId = Long.parseLong(patientId);
        Patient formPatient = patientService.findPatientById(pId);
        mav.addObject("patient", formPatient);
        mav.addObject("drugs", drugService.findAllDrugs());
        return mav;
    }

    @PostMapping("/update/patient/{id}")
    public String updatePatient(Patient patient, @PathVariable("id") String id,
                                @ModelAttribute(value = "drugs") Long drugId) {
        Long pId = Long.parseLong(id);
        Patient updatedPatient = patientService.findPatientById(pId);
        Drug drug = drugService.findDrugById(drugId);
        updatedPatient.setEmail(patient.getEmail());
        updatedPatient.setPhone(patient.getPhone());
        updatedPatient.setCity(patient.getCity());
        // update patient symptoms

        if (patient.getPatientsDrugs() == null ) {
            patient.setPatientsDrugs(new ArrayList<Drug>());
        }
        updatedPatient.getPatientsDrugs().add(drug);
        patientService.updatePatient(updatedPatient);
        return "redirect:/allPatients";
    }


    @GetMapping("delete/{id}")
    public String deletePatientById(@PathVariable("id") String patientId) {
        Long pId = Long.parseLong(patientId);
        Patient deletedPatient = patientService.findPatientById(pId);
        patientService.deletePatient(deletedPatient);
        return "redirect:/allPatients";
    }


}
