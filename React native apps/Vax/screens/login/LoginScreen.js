import { View, Text, Button } from 'react-native'
import React from 'react'

const LoginScreen = ({navigation}) => {
  return (
    <View>
      <Button title='LoginScreen' onPress={() => {
        navigation.replace("MainScreen");
      }}></Button>
    </View>
  )
}

export default LoginScreen