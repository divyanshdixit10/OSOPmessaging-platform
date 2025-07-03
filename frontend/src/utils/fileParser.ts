import Papa, { ParseResult as PapaParseResult, ParseError } from 'papaparse';
import * as XLSX from 'xlsx';
import { Recipient } from '../types/EmailRequest';
import { validateEmail } from './emailValidator';

interface ParseOptions {
  emailColumn?: string;
  nameColumn?: string;
  variableColumns?: string[];
}

type ParseResult = PapaParseResult<Record<string, any>>;

export const parseCSV = (file: File, options: ParseOptions = {}): Promise<Recipient[]> => {
  return new Promise((resolve, reject) => {
    Papa.parse<Record<string, any>>(file, {
      header: true,
      skipEmptyLines: true,
      complete: (results: ParseResult) => {
        const recipients: Recipient[] = results.data
          .map((row: Record<string, any>) => {
            const email = options.emailColumn 
              ? row[options.emailColumn] 
              : (row.email || row.Email || row.EMAIL || Object.values(row)[0]);
            
            const name = options.nameColumn 
              ? row[options.nameColumn]
              : (row.name || row.Name || row.NAME);

            const variables: Record<string, string> = {};
            if (options.variableColumns) {
              options.variableColumns.forEach(col => {
                if (row[col]) variables[col] = row[col];
              });
            }

            return {
              email: email?.toString().trim() || '',
              name: name?.toString().trim(),
              variables,
              isValid: validateEmail(email)
            };
          })
          .filter((recipient: Recipient) => recipient.email);

        resolve(recipients);
      },
      error: (error: Error) => {
        reject(new Error(`Failed to parse CSV: ${error.message}`));
      }
    });
  });
};

export const parseExcel = async (file: File, options: ParseOptions = {}): Promise<Recipient[]> => {
  try {
    const data = await file.arrayBuffer();
    const workbook = XLSX.read(data);
    const worksheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsonData = XLSX.utils.sheet_to_json<Record<string, any>>(worksheet);

    return jsonData.map((row) => {
      const email = options.emailColumn 
        ? row[options.emailColumn] 
        : (row.email || row.Email || row.EMAIL || Object.values(row)[0]);
      
      const name = options.nameColumn 
        ? row[options.nameColumn]
        : (row.name || row.Name || row.NAME);

      const variables: Record<string, string> = {};
      if (options.variableColumns) {
        options.variableColumns.forEach(col => {
          if (row[col]) variables[col] = row[col].toString();
        });
      }

      return {
        email: email?.toString().trim() || '',
        name: name?.toString().trim(),
        variables,
        isValid: validateEmail(email)
      };
    }).filter((recipient: Recipient) => recipient.email);
  } catch (error) {
    throw new Error(`Failed to parse Excel file: ${error instanceof Error ? error.message : 'Unknown error'}`);
  }
};

export const getFileColumns = async (file: File): Promise<string[]> => {
  if (file.name.endsWith('.csv')) {
    return new Promise((resolve, reject) => {
      Papa.parse<Record<string, any>>(file, {
        header: true,
        preview: 1,
        skipEmptyLines: true,
        complete: (results: ParseResult) => {
          resolve(Object.keys(results.data[0] || {}));
        },
        error: (error: Error) => {
          reject(new Error(`Failed to get CSV columns: ${error.message}`));
        }
      });
    });
  } else if (file.name.match(/\.xlsx?$/)) {
    const data = await file.arrayBuffer();
    const workbook = XLSX.read(data);
    const worksheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsonData = XLSX.utils.sheet_to_json<Record<string, any>>(worksheet, { header: 1 });
    return (jsonData[0] as string[]) || [];
  }
  return [];
}; 