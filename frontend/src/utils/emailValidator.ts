import { Recipient } from '../types/EmailRequest';

const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

export const validateEmail = (email: string): boolean => {
  return EMAIL_REGEX.test(email.trim());
};

export const validateRecipients = (recipients: Recipient[]): Recipient[] => {
  return recipients.map(recipient => ({
    ...recipient,
    email: recipient.email.trim(),
    isValid: validateEmail(recipient.email)
  }));
};

export const extractEmailsFromText = (text: string): Recipient[] => {
  // Split by commas or newlines
  const emails = text.split(/[,\n]/)
    .map(email => email.trim())
    .filter(email => email.length > 0);

  return emails.map(email => ({
    email,
    isValid: validateEmail(email)
  }));
};

export const removeDuplicateEmails = (recipients: Recipient[]): Recipient[] => {
  const uniqueEmails = new Map<string, Recipient>();
  
  recipients.forEach(recipient => {
    const email = recipient.email.toLowerCase().trim();
    if (!uniqueEmails.has(email) || recipient.isValid) {
      uniqueEmails.set(email, recipient);
    }
  });

  return Array.from(uniqueEmails.values());
}; 