# Multi-Channel Messaging Platform Frontend

A modern React TypeScript frontend for sending messages through multiple channels (Email, SMS, WhatsApp).

## Features

- 📱 Multiple messaging channels (Email, SMS, WhatsApp)
- 📎 File attachments and media URL support
- ✨ Modern, responsive UI with Chakra UI
- 🔍 Form validation with Formik and Yup
- 📄 Support for multiple recipients
- 🚀 Real-time feedback with toast notifications

## Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)
- Backend server running on http://localhost:8080

## Installation

1. Clone the repository
2. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
3. Install dependencies:
   ```bash
   npm install
   ```

## Development

To start the development server:

```bash
npm start
```

The application will be available at http://localhost:3000

## Building for Production

To create a production build:

```bash
npm run build
```

The build artifacts will be stored in the `build/` directory.

## Environment Variables

Create a `.env` file in the frontend directory with the following variables:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

## Tech Stack

- React with TypeScript
- Chakra UI for styling
- Formik & Yup for form handling and validation
- Axios for API calls
- React Dropzone for file uploads
- React Toastify for notifications

## Project Structure

```
src/
├── components/         # React components
├── api/               # API service layer
├── types/             # TypeScript interfaces
├── utils/             # Utility functions
└── App.tsx            # Main application component
```

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
