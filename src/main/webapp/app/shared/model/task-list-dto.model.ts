export class TaskListDto {
  id: string;
  taskDefinitionId: string;
  name: string;
  processName: string;
  taskState: string;
  assignee: string;
  formId: string;
  processDefinitionKey: string;
  formVersion: string;

  // Optionally, you can add a constructor
  constructor() {
    this.id = '';
    this.taskDefinitionId = '';
    this.name = '';
    this.processName = '';
    this.taskState = '';
    this.assignee = '';
    this.formId = '';
    this.processDefinitionKey = '';
    this.formVersion = '';
  }
}

// Define a default value
export const defaultValue: TaskListDto = new TaskListDto();
