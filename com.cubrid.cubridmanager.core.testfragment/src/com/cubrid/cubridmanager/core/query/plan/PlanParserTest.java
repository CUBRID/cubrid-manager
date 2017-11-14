package com.cubrid.cubridmanager.core.query.plan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.cubrid.common.core.queryplan.PlanParser;
import com.cubrid.common.core.queryplan.model.PlanCost;
import com.cubrid.common.core.queryplan.model.PlanNode;
import com.cubrid.common.core.queryplan.model.PlanResult;
import com.cubrid.common.core.queryplan.model.PlanTerm;
import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;

import junit.framework.TestCase;

public class PlanParserTest extends
		TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public String getFilePath(String filepath) {
		URL fileUrl = null;
		if (CubridManagerCorePlugin.getDefault() == null) {
			fileUrl = this.getClass().getResource(filepath);
		} else {
			Bundle bundle = CubridManagerCorePlugin.getDefault().getBundle();
			URL url = bundle.getResource(filepath);
			try {
				fileUrl = FileLocator.toFileURL(url);
			} catch (IOException e) {
				return null;
			}
		}
		return fileUrl == null ? null : fileUrl.getPath();
	}

	protected String loadPlanExample(String filepath) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			StringBuilder sb = new StringBuilder();
			fr = new FileReader(new File(
					getFilePath("/com/cubrid/cubridmanager/core/query/plan/"
							+ filepath)));
			br = new BufferedReader(fr);
			for (;;) {
				String line = br.readLine();
				if (line == null)
					break;
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception ex) {
			return null;
		} finally {
			try {
				br.close();
			} catch (Exception ignored) {
			}
			try {
				fr.close();
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * simple plan
	 * 
	 * @throws Exception
	 */
	public void testExam01() throws Exception {
		String planString = loadPlanExample("plan01.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertTrue(bool);
		parser.getPlanTree(1);
		PlanResult planRoot = parser.getPlanTree(0);
		assertNotNull(planRoot);

		PlanNode planNode = planRoot.getPlanNode();

		assertEquals(planNode.getMethod(), "idx-join (inner join)");
		assertEquals(planNode.getDepth(), 1);

		PlanCost planCost = planNode.getCost();
		assertNotNull(planCost);
		assertEquals(planCost.getCard(), 40);
		assertEquals(String.valueOf(planCost.getFixedCpu()), "0.0");
		assertEquals(String.valueOf(planCost.getFixedDisk()), "2.0");
		assertEquals(String.valueOf(planCost.getFixedTotal()), "2.0");
		assertEquals(String.valueOf(planCost.getVarCpu()), "100.3");
		assertEquals(String.valueOf(planCost.getVarDisk()), "275.0");
		assertEquals(String.valueOf(planCost.getVarTotal()), "375.0");

		assertNull(planNode.getTable());
		assertNull(planNode.getIndex());
		assertNull(planNode.getEdge());
		assertNull(planNode.getFilter());
		assertNull(planNode.getSort());
		assertNull(planNode.getOrder());

		PlanTerm sargs = planNode.getSargs();
		assertNotNull(sargs);

		assertNotNull(planNode.getChildren());
		assertEquals(planNode.getChildren().size(), 2);
		PlanParser parser2 = new PlanParser();
		parser2.getPlanTree(0);
		parser2.countPlanTree();
		parser2.doParse(null);
		parser2.doParse("aaa");
		
		
	}

	/**
	 * complicated plan
	 * 
	 * @throws Exception
	 */
	public void testExam02() throws Exception {
		String planString = loadPlanExample("plan02.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertTrue(bool);

		int subPlanCount = parser.countPlanTree();
		assertEquals(10, subPlanCount);

		for (int i = 0; i < subPlanCount; i++) {
			PlanResult planRoot = parser.getPlanTree(i);
			assertNotNull(planRoot);
		}
	}

	/**
	 * Partitioned table
	 * 
	 * @throws Exception
	 */
	public void testExam03() throws Exception {
		String planString = loadPlanExample("plan03.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertTrue(bool);

		int subPlanCount = parser.countPlanTree();
		assertEquals(1, subPlanCount);

		for (int i = 0; i < subPlanCount; i++) {
			PlanResult planRoot = parser.getPlanTree(i);
			assertNotNull(planRoot);
		}
	}
	
	/**
	 * Partitioned table
	 * 
	 * @throws Exception
	 */
	public void testExam04() throws Exception {
		String planString = loadPlanExample("plan04.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertFalse(bool);
	}
	
	/**
	 * Partitioned table
	 * 
	 * @throws Exception
	 */
	public void testExam05() throws Exception {
		String planString = loadPlanExample("plan05.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertFalse(bool);
	}

	/**
	 * Partitioned table
	 * 
	 * @throws Exception
	 */
	public void testExam06() throws Exception {
		String planString = loadPlanExample("plan06.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertFalse(bool);
	}

	/**
	 * Partitioned table
	 * 
	 * @throws Exception
	 */
	public void testExam07() throws Exception {
		String planString = loadPlanExample("plan07.txt");

		PlanParser parser = new PlanParser();
		boolean bool = parser.doParse(planString);
		assertFalse(bool);
		
	}

}
