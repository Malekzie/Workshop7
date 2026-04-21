// Contributor(s): Robbie
// Main: Robbie - JPA entity or embed for auth chat messaging or staff domain.

package com.sait.peelin.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmployeeSpecialtyId implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String category;
}
