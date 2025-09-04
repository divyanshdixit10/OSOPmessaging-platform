# OSOP Messaging Platform - Frontend

A professional, modern frontend interface for the OSOP Messaging Platform built with React, TypeScript, and Chakra UI.

## Features

### 🎯 **Professional Dashboard**
- Real-time campaign statistics and metrics
- Campaign progress tracking
- Recent activity feed
- Quick action buttons for common tasks

### 📧 **Advanced Email Campaign Builder**
- Rich email editor with preview functionality
- Template management and selection
- Bulk recipient management
- File attachment support
- Media URL integration
- Campaign progress tracking
- Advanced tracking options (opens, clicks, unsubscribes)

### 📋 **Template Management**
- Create, edit, and manage email templates
- Template categorization (newsletter, welcome, promotional, transactional)
- Template duplication and organization
- Search and filter functionality

### 📊 **Analytics & Performance**
- Comprehensive email campaign analytics
- Performance metrics and trends
- Campaign comparison tools
- Engagement tracking
- Export capabilities

### ⚙️ **Settings & Configuration**
- User profile management
- Notification preferences
- Security settings (2FA, session management)
- Platform information and system status

### 🎨 **Modern UI/UX**
- Responsive design for all devices
- Professional color scheme and typography
- Intuitive navigation with sidebar
- Card-based layout system
- Interactive components and animations

## Technology Stack

- **React 18** - Modern React with hooks
- **TypeScript** - Type-safe development
- **Chakra UI** - Professional component library
- **React Router** - Client-side routing
- **React Icons** - Beautiful icon set
- **Framer Motion** - Smooth animations

## Getting Started

### Prerequisites
- Node.js 16+ 
- npm or yarn

### Installation

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Start development server:**
   ```bash
   npm start
   ```

3. **Build for production:**
   ```bash
   npm run build
   ```

### Environment Variables

Create a `.env` file in the frontend directory:

```env
REACT_APP_API_URL=http://localhost:8080
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── layout/         # Layout components (AppLayout)
│   └── ...            # Other components
├── pages/              # Page components
│   ├── DashboardPage   # Main dashboard
│   ├── SendEmailPage   # Email campaign builder
│   ├── TemplatesPage   # Template management
│   ├── AnalyticsPage   # Analytics and reporting
│   └── SettingsPage    # User settings
├── api/                # API service functions
├── types/              # TypeScript type definitions
├── theme.ts            # Custom Chakra UI theme
└── App.tsx             # Main application component
```

## Key Components

### AppLayout
- Responsive sidebar navigation
- Header with user menu
- Mobile-friendly drawer navigation

### DashboardPage
- Statistics cards with trend indicators
- Campaign progress tracking
- Recent activity timeline
- Quick action buttons

### SendEmailPage
- Campaign overview with real-time stats
- Email composition with preview
- Recipient management
- Advanced tracking options
- Progress tracking during sending

### TemplatesPage
- Template grid with search and filters
- Template creation and editing modal
- Category-based organization
- Template duplication and management

### AnalyticsPage
- Performance metrics dashboard
- Campaign comparison tables
- Engagement analytics
- Export functionality

### SettingsPage
- User profile management
- Notification preferences
- Security settings
- Platform information

## Design System

### Color Palette
- **Primary**: Blue (#0ea5e9)
- **Success**: Green (#10b981)
- **Warning**: Orange (#f59e0b)
- **Error**: Red (#ef4444)
- **Neutral**: Gray scale (#f8fafc to #0f172a)

### Typography
- **Font Family**: Inter (system fallbacks)
- **Headings**: Bold weights for hierarchy
- **Body**: Medium weight for readability

### Components
- **Cards**: Consistent spacing and shadows
- **Buttons**: Brand color scheme with variants
- **Forms**: Clean, accessible form controls
- **Tables**: Professional data presentation

## Responsive Design

- **Mobile First**: Optimized for mobile devices
- **Breakpoints**: Responsive grid system
- **Navigation**: Collapsible sidebar for mobile
- **Touch Friendly**: Optimized for touch interactions

## Performance Features

- **Lazy Loading**: Route-based code splitting
- **Optimized Images**: Efficient image handling
- **Smooth Animations**: 60fps animations with Framer Motion
- **Efficient Rendering**: React optimization best practices

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Follow the existing code style and patterns
2. Use TypeScript for all new code
3. Implement responsive design for all components
4. Add proper error handling and loading states
5. Include accessibility features (ARIA labels, keyboard navigation)

## Future Enhancements

- **Real-time Charts**: Integration with charting libraries
- **Advanced Analytics**: Machine learning insights
- **A/B Testing**: Campaign optimization tools
- **Automation**: Workflow automation features
- **Integrations**: Third-party service connections

## Support

For technical support or questions about the frontend:
- Check the documentation
- Review the code comments
- Contact the development team

---

Built with ❤️ by the OSOP Development Team
