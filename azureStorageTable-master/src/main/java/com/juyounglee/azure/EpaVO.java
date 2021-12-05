package com.juyounglee.azure;

import com.microsoft.azure.storage.table.TableServiceEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EpaVO extends TableServiceEntity {

	String id;
	String fac_id;
	String eqp_id;
	String area;
	int chamber;
}
