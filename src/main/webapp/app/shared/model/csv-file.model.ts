import dayjs from 'dayjs';

export interface ICsvFile {
  id?: number;
  file?: string;
  createdDate?: dayjs.Dayjs;
  updatedDate?: dayjs.Dayjs;
}

export const defaultValue: Readonly<ICsvFile> = {};
