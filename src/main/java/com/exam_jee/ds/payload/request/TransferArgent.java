package com.exam_jee.ds.payload.request;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransferArgent {
    private Long clientId;
    private String beneficiaireId;
    private double montant;
}
