import { useState } from 'react';
import { View, Text, TextInput, Image, TouchableOpacity, ScrollView } from "react-native";
import styles from './authentication.style';
import AppButton from '../../components/AppButton';
import GetAppWrapper from '../../components/GetAppWrapper';
import MiddleAccountWrapper from '../../components/MiddleAccountWrapper';
import AppText from '../../components/AppText.jsx';
import ORWrapper from '../../components/ORWrapper';

const LoginScreen = ({ navigation }) => {
  const [userName, setUserName] = useState("")
  const [password, setPassword] = useState("")

  return (
    <ScrollView >
      <View style={styles.parent}>
        <View style={styles.container}>
          <Image
            source={require("../../../assets/images/instagramText.png")}
          />
          <TextInput
            style={styles.inputTextStyle}
            value={userName}
            onChangeText={setUserName}
            placeholder="Phone number, username, or email"
          />
          <TextInput
            style={styles.inputTextStyle}
            value={password}
            onChangeText={setPassword}
            placeholder="Password"
          />
          <AppButton
            buttonName={"Log in"}
            onPress={() => { console.log("Tap") }}
          />

          <ORWrapper />
          <TouchableOpacity>
            <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'center', marginBottom: 20 }}>
              <Image source={require('../../../assets/images/facebook.png')} style={{ width: 24, height: 24 }} />
              <AppText
                style={{ marginStart: 6, color: '#385185', fontWeight: '700' }} >Log in with Facebook</AppText>
            </View>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => navigation.navigate('ResetPasswordScreen')}>
            <AppText
              style={{ color: '#385185', opacity: 0.9, fontWeight: '600', marginBottom: 20 }} fontType='bold' >Forgot password?</AppText>
          </TouchableOpacity>
        </View>
        <MiddleAccountWrapper
          name1={"Don't have an account?"}
          name2={"Sign up"}
          onPress={() => navigation.navigate('RegisterScreen')} />
        <GetAppWrapper />
      </View>
    </ScrollView>
  )
};


export default LoginScreen;
