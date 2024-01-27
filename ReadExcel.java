package org.example;

import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReadExcel {
	private static final int SHEET_INDEX = 0;
	private static final int BATCH_SIZE = 1000;
	private static final String QUERY = "INSERT INTO animal_for_nickname (name) VALUES (?)";

	private final JdbcTemplate jdbcTemplate;

	public void start() {
		final List<String> responses = new ArrayList<>();
		try {
			FileInputStream file = new FileInputStream("/Users/gyucheol/Downloads/202309.xlsx");
			Workbook workbook = WorkbookFactory.create(file);

			Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
			// 행의 수
			int rows = sheet.getPhysicalNumberOfRows();

			for (int rowindex = 2; rowindex < rows; rowindex++) {
				// 행을 읽는다
				Row row = sheet.getRow(rowindex);
				if (row != null) {
					// 셀의 수
					Cell nameCell = row.getCell(3);
					if (nameCell == null) {
						continue;
					}
					String name = nameCell.getStringCellValue();

					if (name.isBlank()) {
						continue;
					}
					responses.add(name);

					// 1000개 단위로 DB에 삽입
					if (responses.size() % BATCH_SIZE == 0) {
						insertBatch(jdbcTemplate, responses);
						responses.clear();
					}
				}
			}
			// 남은 데이터 처리
			if (!responses.isEmpty()) {
				insertBatch(jdbcTemplate, responses);
				responses.clear();
			}

			log.info("엑셀 파일 읽기 완료!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertBatch(JdbcTemplate jdbcTemplate, List<?> responses) {
		jdbcTemplate.batchUpdate(QUERY, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				var response = (String)responses.get(i);
				ps.setString(1, response);
			}

			@Override
			public int getBatchSize() {
				return responses.size();
			}
		});
	}
}
