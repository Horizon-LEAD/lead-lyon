package kpi;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

public class ReadHbefa {
	static public void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		String path = "/home/shoerl/Downloads/HBEFA41_PubVersion_Oct2019_updatedFiles_64bit/HBEFA41_System.mde";

		Database db = DatabaseBuilder.open(new File(path));

		for (String name : db.getTableNames()) {
			// if (name.contains("EFA")) {
			System.out.println(name);
			// }
		}

		System.out.println();

		// System.exit(1);

		List<String> names = Arrays.asList("B_EFA_ColdStart", "B_EFA_ColdStart_New", "B_CS_EFA", "B_EFA_Evap",
				"B_EFA_Evap_New", "B_EFA_New", "B_EFA_new_AvrgSpeed", "B_TS_HBEFA21");

		names = Arrays.asList("B_EFA_ColdStart");

		for (String name : names) {
			System.out.print(name + ": ");

			List<String> cols = new LinkedList<>();

			for (Column column : db.getTable(name).getColumns()) {
				if (!column.getName().startsWith("ID")) {
					System.out.print(column.getName() + " ");
					cols.add(column.getName());
				}
			}

			System.out.println("--------------");

			Cursor cursor = db.getTable(name).newCursor().toCursor();

			while (cursor.moveToNextRow()) {
				var row = cursor.getCurrentRow();

				for (int i = 0; i < cols.size(); i++) {
					System.out.print(row.get(cols.get(i)) + " ");
				}
				
				System.out.println();
			}			
		}

	}
}
