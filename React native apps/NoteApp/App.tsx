import { NavigationContainer } from '@react-navigation/native';
import { NativeStackNavigationProp, createNativeStackNavigator } from '@react-navigation/native-stack';
import React from 'react';
import NotesScreen from './shared/screens/NotesScreen';
import CreateNoteScreen from './shared/screens/CreateNoteScreen';
import { Pressable, Text } from 'react-native';
import SaveButton from './shared/components/SaveButton';
import AddNoteButton from './shared/components/AddNoteButton';

export type StackParamList = {
  NotesScreen: undefined;
  CreateNoteScreen: undefined;
}

const Stack = createNativeStackNavigator<StackParamList>();

export type ScreenNavigationStackProp =
  NativeStackNavigationProp<StackParamList>;

const App: React.FC = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{
        headerShadowVisible:false,
        headerTitleAlign:'center'
      }}>
        <Stack.Screen
          name='NotesScreen'
          component={NotesScreen}
          options={
            {
              headerTitle: "Notes",
              headerRight: () => (
                <AddNoteButton
                />
              )

            }
          }
        />
        <Stack.Screen
          name='CreateNoteScreen'
          component={CreateNoteScreen}
          options={
            {
              headerTitle: "Add Note",
              headerRight: () => (
                <SaveButton
                />
              )

            }
          }
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default App;
