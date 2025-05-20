package com.university.beans;

import com.university.entity.Student;
import com.university.service.OptimisticLockDemoService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@SessionScoped
public class OptLockDemoBean implements Serializable {
    private static final Logger logger = Logger.getLogger(OptLockDemoBean.class.getName());
    private static final long serialVersionUID = 1L;

    @Inject
    private OptimisticLockDemoService demoService;

    @Inject
    private StudentBean studentBean;

    private Long selectedStudentId;
    private Student originalStudent;
    private Long originalVersion;
    private String results = "";
    private int currentStep = 0;

    @PostConstruct
    public void init() {
        reset();
    }

    public void reset() {
        selectedStudentId = null;
        originalStudent = null;
        originalVersion = null;
        results = "";
        currentStep = 0;
    }

    public String selectStudent(Long id) {
        try {
            reset();

            if (id == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Warning", "No student ID provided"));
                return null;
            }

            originalStudent = demoService.getStudent(id);

            if (originalStudent == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", "Student not found with ID " + id));
                return null;
            }

            selectedStudentId = id;
            originalVersion = originalStudent.getVersion();
            currentStep = 1;

            results = "STEP 1: Student selected\n";
            results += "ID: " + originalStudent.getId() + "\n";
            results += "Name: " + originalStudent.getFirstName() + " " + originalStudent.getLastName() + "\n";
            results += "Initial version: " + originalVersion + "\n";
            results += "---------------------------\n";

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Student Selected", "Ready for demonstration"));

            return null;
        } catch (Exception e) {
            logger.severe("Error in selectStudent: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", e.getMessage()));
            return null;
        }
    }

    public String runStep2() {
        if (currentStep != 1) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Warning", "Please complete step 1 first"));
            return null;
        }

        try {
            
            Student updated = demoService.updateStudentFirstTime(
                    originalStudent,
                    originalStudent.getFirstName() + " [Updated]");

            
            results += "STEP 2: First update completed\n";
            results += "Updated name: " + updated.getFirstName() + "\n";
            results += "New version: " + updated.getVersion() + "\n";
            results += "---------------------------\n";

            currentStep = 2;

            
            studentBean.init();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Step 2 Complete", "First update successful"));

            return null;
        } catch (Exception e) {
            logger.severe("Error in runStep2: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", e.getMessage()));
            return null;
        }
    }

    public String runStep3() {
        if (currentStep != 2) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Warning", "Please complete step 2 first"));
            return null;
        }

        try {
            
            boolean updated = demoService.forceDirectDatabaseUpdate(
                    selectedStudentId,
                    " [Modified Directly]");

            if (!updated) {
                results += "STEP 3: ERROR - Direct database update failed\n";
                results += "---------------------------\n";

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error", "Failed to perform direct database update"));
                return null;
            }

            results += "STEP 3: Direct database update completed\n";
            results += "The database version was incremented directly\n";
            results += "This simulates another user modifying the record\n";
            results += "---------------------------\n";

            currentStep = 3;

            
            studentBean.init();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Step 3 Complete", "Direct database update successful"));

            return null;
        } catch (Exception e) {
            logger.severe("Error in runStep3: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", e.getMessage()));
            return null;
        }
    }

    public String runStep4() {
        if (currentStep != 3) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Warning", "Please complete step 3 first"));
            return null;
        }

        try {
            
            String result = demoService.tryUpdateWithStaleVersion(
                    originalStudent,
                    originalStudent.getFirstName() + " [Should Fail]");

            results += "STEP 4: Attempted update with stale version\n";
            results += "Original version: " + originalVersion + "\n";
            results += "Result: " + result + "\n";
            results += "---------------------------\n";

            currentStep = 4;

            
            studentBean.init();

            if (result.contains("OPTIMISTIC LOCK EXCEPTION")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Step 4 Complete", "OptimisticLockException occurred as expected"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Warning", "Optimistic locking did not work as expected"));
            }

            return null;
        } catch (Exception e) {
            logger.severe("Error in runStep4: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", e.getMessage()));
            return null;
        }
    }

    public String runStep5() {
        if (currentStep != 4) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Warning", "Please complete step 4 first"));
            return null;
        }

        try {
            
            String result = demoService.recoverFromOptimisticLock(
                    selectedStudentId,
                    originalStudent.getFirstName() + " [Recovered]");

            results += "STEP 5: Recovery demonstration\n";
            results += "Result: " + result + "\n";
            results += "---------------------------\n";

            currentStep = 5;

            studentBean.init();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Step 5 Complete", "Recovery demonstration completed"));

            return null;
        } catch (Exception e) {
            logger.severe("Error in runStep5: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", e.getMessage()));
            return null;
        }
    }

    public String startOver() {
        try {
            
            if (selectedStudentId != null) {
                demoService.resetStudentName(selectedStudentId);
            } else {
                
                
                demoService.resetAllStudentNames();
            }

            
            reset();

            
            studentBean.init();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Reset Complete", "Ready for a new demonstration"));

            return "optimisticLockingDemo?faces-redirect=true";
        } catch (Exception e) {
            logger.severe("Error in startOver: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Failed to reset: " + e.getMessage()));
            return null;
        }
    }

    public String getResults() {
        return results;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public Long getSelectedStudentId() {
        return selectedStudentId;
    }

    public void setSelectedStudentId(Long selectedStudentId) {
        this.selectedStudentId = selectedStudentId;
    }
}