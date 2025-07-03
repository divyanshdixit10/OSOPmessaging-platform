import React from 'react';
import { ChakraProvider, CSSReset } from '@chakra-ui/react';
import SendEmailPage from './pages/SendEmailPage';

function App() {
  return (
    <ChakraProvider>
      <CSSReset />
      <SendEmailPage />
    </ChakraProvider>
  );
}

export default App;
