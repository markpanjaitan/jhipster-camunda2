export interface ITaskList {
  id?: number;
  taskDefinitionId?: string;
  name?: string;
  processName?: string;
  taskState?: string | null;
}

export const defaultValue: Readonly<ITaskList> = {};
