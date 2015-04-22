package com.vng.util;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.*;

import org.apache.poi.*;
import org.apache.poi.ss.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.*;
import org.apache.poi.hssf.usermodel.*;

public class ExcelData
{
	private static HashMap<String, SheetData>		_all_sheet = null;
	
	
	public static boolean LoadExcelData(String path)
	{
		//get all anim file in folder
		Path dir = Paths.get(path);
		
                
		if (Files.isDirectory(dir) == false)
		{
			return false;
		}
                
                
		XLSFileVisitor xls_finder = new XLSFileVisitor();
		try
		{
			EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
			Files.walkFileTree(dir, opts, 1, xls_finder);
		}
		catch (Exception ex)
		{
			return false;
		}
                
        _all_sheet = new HashMap<String, SheetData>();
		
		for (Path p : xls_finder._list_xls_files)
		{
			//get file name no ext
			String filename = p.toString();
			int idx = filename.lastIndexOf('\\');
			if (idx >= 0)
			{
				filename = filename.substring(idx + 1);
			}
			idx = filename.lastIndexOf('/');
			if (idx >= 0)
			{
				filename = filename.substring(idx + 1);
			}
			idx = filename.indexOf('.');
			if (idx >= 0)
			{
				filename = filename.substring(0, idx);
			}
			filename = filename.toLowerCase();
			
			//load data
			SheetData sd = new SheetData();
			
			System.out.println(p); 
			
			if (sd.LoadSheetData(p))
			{
				_all_sheet.put(filename, sd);
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}

	public static Object GetData(String file, String row_name, String column_name)
	{
		SheetData sd = _all_sheet.get(file);
		
		return sd.GetData(row_name, column_name);
	}

	public static Object GetData(String file, String sheet_name, String row_name, String column_name)
	{
		SheetData sd = _all_sheet.get(file);
		
		return sd.GetData(sheet_name, row_name, column_name);
	}
	
	public static Object[][][] GetData(String file)
	{
		SheetData sd = _all_sheet.get(file);
		
		if (sd == null)
		{
			System.out.println("File " + file + " is not existed!"); 
			return null;
		}
		
		return sd.GetData();
	}	
}

class SheetData
{
	private Object[][][]				_data = null;
	private HashMap<String, Integer>	_sheet = null;
	private HashMap<String, Integer>	_row = null;
	private HashMap<String, Integer>	_column = null;
	
	public SheetData()
	{
		_sheet = new HashMap<String, Integer>();
		_row = new HashMap<String, Integer>();
		_column = new HashMap<String, Integer>();
	}
	
	public boolean LoadSheetData(Path file)
	{
		try
		{
			//Open text file
			InputStream is = null;
			is = Files.newInputStream(file);
			
			HSSFWorkbook workbook =  new HSSFWorkbook(is);
			
			int num_sheets = workbook.getNumberOfSheets();
            // System.out.println("num sheets: " + num_sheets);

			_data = new Object[num_sheets][][];
			
			for (int sh = 0; sh < num_sheets; sh++)
			{
				//get first sheet
				HSSFSheet sheet = workbook.getSheetAt(sh);
				
				String _sheet_name = workbook.getSheetName(sh);
				_sheet.put(_sheet_name, new Integer(sh));
				
				HSSFCell cell = null;
				
				//get first row
				int first_row_index = sheet.getFirstRowNum();
				//get last row
				int last_row_index = sheet.getLastRowNum();
				
				if (first_row_index == last_row_index)
				{
					continue;
				}
				
				HSSFRow first_row = sheet.getRow(first_row_index);
				//get first column
				int first_cell = first_row.getFirstCellNum();
				int last_cell = first_row.getLastCellNum() - 1;
				
				//get column name
				for (int j = first_cell; j <= last_cell; j++)
				{
					cell = first_row.getCell(j);
					String col_name = cell.getStringCellValue();
					
					_column.put(col_name, new Integer(j - first_cell));
				}
				
				int num_data_rows = last_row_index - first_row_index;
				int num_data_cols = last_cell - first_cell + 1;
				
				_data[sh] = new Object[num_data_rows][];
				
				try
				{
					//get data and row name
					for (int i = 0; i < num_data_rows; i++)
					{
						HSSFRow row = sheet.getRow(i + first_row_index + 1);
						
						//get first cell
						cell = row.getCell(first_cell - 1);
						
						//get row name
						String row_name = cell.getStringCellValue();
						
						_row.put(row_name, new Integer(i));
						
						_data[sh][i] = new Object[num_data_cols];
						
						for (int j = 0; j < num_data_cols; j++)
						{
							cell = row.getCell(j + first_cell);
												
							int cell_type = cell.getCellType();
												
							if (cell_type == Cell.CELL_TYPE_STRING)
							{
								String str_val = cell.getStringCellValue();
								_data[sh][i][j] = str_val;
							}
							else if (cell_type == Cell.CELL_TYPE_NUMERIC)
							{
								double number_val = cell.getNumericCellValue();
								_data[sh][i][j] = new Double(number_val);
								
								// _data[sh][i][j] = (long)cell.getNumericCellValue();
							}
							else
							{
								System.out.println("LoadSheetData: err! at [" + sh + "][" + i + "][" + j + "]: " + cell_type);
								return false;
							}
						}
					}
				}
				catch (Exception e)
				{
					System.out.println("\nError at sheet: " + _sheet_name + '\n'); 
					e.printStackTrace();
					return false;
				}
			}
					
			is.close();
		}
		catch (Exception ex)
		{
            ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public Object GetData(String row, String column)
	{
		Integer row_id = _row.get(row);
		Integer col_id = _column.get(column);
		
		return _data[0][row_id][col_id];
	}
	
	public Object GetData(String sheet, String row, String column)
	{
		Integer sheet_id = _sheet.get(sheet);
		Integer row_id = _row.get(row);
		Integer col_id = _column.get(column);
		
		return _data[sheet_id][row_id][col_id];
	}
	
	public Object[][][] GetData()
	{
		return _data;
	}	
}

class XLSFileVisitor extends SimpleFileVisitor<Path> 
{
	public LinkedList<Path>	_list_xls_files = null;
	
	public XLSFileVisitor()
	{
		_list_xls_files = new LinkedList<Path>();
	}
	
	// Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) 
	{
		//add to list
		String filename = file.toString();
		
		if (filename.endsWith(".xls"))
		{
			_list_xls_files.add(file);
		}
		
        return FileVisitResult.CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) 
	{	
        return FileVisitResult.CONTINUE;
    }
}