import axios from './axios';
import { EmailTemplate } from '../types/EmailTemplate';

const STORAGE_KEY = 'emailTemplates';

// Local storage functions
const getStoredTemplates = (): EmailTemplate[] => {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored ? JSON.parse(stored) : [];
};

const setStoredTemplates = (templates: EmailTemplate[]) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(templates));
};

// API functions
export const fetchTemplates = async (): Promise<EmailTemplate[]> => {
  try {
    const response = await axios.get('/api/template/all');
    return response.data;
  } catch (error) {
    // Fallback to local storage if API fails
    return getStoredTemplates();
  }
};

export const saveTemplate = async (template: EmailTemplate): Promise<EmailTemplate> => {
  try {
    const response = await axios.post('/api/template/save', template);
    return response.data;
  } catch (error) {
    // Fallback to local storage if API fails
    const templates = getStoredTemplates();
    const newTemplate = {
      ...template,
      id: `local_${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    templates.push(newTemplate);
    setStoredTemplates(templates);
    return newTemplate;
  }
};

export const updateTemplate = async (template: EmailTemplate): Promise<EmailTemplate> => {
  try {
    const response = await axios.put(`/api/template/${template.id}`, template);
    return response.data;
  } catch (error) {
    // Fallback to local storage if API fails
    const templates = getStoredTemplates();
    const index = templates.findIndex(t => t.id === template.id);
    if (index !== -1) {
      const updatedTemplate = {
        ...template,
        updatedAt: new Date().toISOString()
      };
      templates[index] = updatedTemplate;
      setStoredTemplates(templates);
      return updatedTemplate;
    }
    throw new Error('Template not found');
  }
};

export const deleteTemplate = async (templateId: string): Promise<void> => {
  try {
    await axios.delete(`/api/template/${templateId}`);
  } catch (error) {
    // Fallback to local storage if API fails
    const templates = getStoredTemplates();
    const filteredTemplates = templates.filter(t => t.id !== templateId);
    setStoredTemplates(filteredTemplates);
  }
}; 