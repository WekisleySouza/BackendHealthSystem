package com.project.healthsystem.repository.projections;

import java.util.List;

public interface PatientInfoAppointmentsProjection {

    List<PatientInfoAppointmentProjection> getAppointments();
}
