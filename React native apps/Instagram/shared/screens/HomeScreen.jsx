import { useState } from 'react';
import { View, StyleSheet, Text } from "react-native";
import AppText from '../components/AppText';


const HomeScreen = ({ navigation }) => {

  return <View style={styles.container}>
    <AppText style={{ fontSize: 18, fontWeight: 'bold' }}>Home Screen</AppText>
  </View>
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});


export default HomeScreen;