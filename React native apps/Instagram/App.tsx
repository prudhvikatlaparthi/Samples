import * as React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from './shared/screens/authentication/LoginScreen';
import HomeScreen from './shared/screens/HomeScreen';
import RegisterScreen from './shared/screens/authentication/RegisterScreen';
import ResetPasswordScreen from './shared/screens/authentication/ResetPasswordScreen';
import BirthdayScreen from './shared/screens/authentication/BirthdayScreen';


const Stack = createNativeStackNavigator();
export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        <Stack.Screen name="LoginScreen" component={LoginScreen} />
        <Stack.Screen name="RegisterScreen" component={RegisterScreen} />
        <Stack.Screen name="BirthdayScreen" component={BirthdayScreen} />
        <Stack.Screen name="ResetPasswordScreen" component={ResetPasswordScreen} />
        <Stack.Screen name="HomeScreen" component={HomeScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}