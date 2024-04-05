import React from 'react'
import { NavigationContainer } from '@react-navigation/native'
import { createDrawerNavigator } from '@react-navigation/drawer'
import HomeScreen from '../home/HomeScreen';
import AboutScreen from '../about/AboutScreen';

const Drawer = createDrawerNavigator();

const MainScreen = () => {
  return (
    <Drawer.Navigator initialRouteName='HomeScreen'>
    <Drawer.Screen name="HomeScreen" component={HomeScreen} />
    <Drawer.Screen name="AboutScreen" component={AboutScreen} />
    </Drawer.Navigator>
  )
}

export default MainScreen